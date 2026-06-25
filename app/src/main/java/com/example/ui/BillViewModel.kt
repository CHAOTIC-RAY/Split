package com.example.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.OCRService
import com.example.ai.LocalLLMService
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class BillViewModel(
    private val repository: BillRepository,
    private val ocrService: OCRService,
    private val localLLMService: LocalLLMService
) : ViewModel() {
    private val json = Json { ignoreUnknownKeys = true }

    val allBills = repository.allBills.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allHomes = repository.allHomes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _extractionState = MutableStateFlow<UiState<BillData>>(UiState.Idle)
    val extractionState: StateFlow<UiState<BillData>> = _extractionState

    private val _modelDownloadState = MutableStateFlow<UiState<Float>>(
        if (localLLMService.isModelDownloaded()) UiState.Success(1f) else UiState.Idle
    )
    val modelDownloadState: StateFlow<UiState<Float>> = _modelDownloadState

    fun downloadModel() {
        viewModelScope.launch {
            _modelDownloadState.value = UiState.Loading
            val success = localLLMService.downloadModel { progress ->
                _modelDownloadState.value = UiState.Loading // Progress could be added to Success or a separate state
            }
            if (success) {
                localLLMService.initialize()
                _modelDownloadState.value = UiState.Success(1f)
            } else {
                _modelDownloadState.value = UiState.Error("Failed to download AI model.")
            }
        }
    }

    data class BillData(val bill: Bill, val items: List<BillItem>)

    @Serializable
    data class ExtractedBill(
        val shopName: String,
        val totalAmount: Double,
        val category: String,
        val items: List<ExtractedItem> = emptyList()
    )

    @Serializable
    data class ExtractedItem(val name: String, val price: Double, val quantity: Int)

    fun scanBill(bitmap: Bitmap) {
        viewModelScope.launch {
            if (!localLLMService.isModelDownloaded()) {
                _extractionState.value = UiState.Error("AI model not downloaded. Please download it first.")
                return@launch
            }

            _extractionState.value = UiState.Loading
            
            // 1. OCR
            val extractedText = ocrService.extractText(bitmap)
            if (extractedText.isBlank()) {
                _extractionState.value = UiState.Error("Could not read any text from the image.")
                return@launch
            }

            // 2. Local LLM Inference
            val prompt = """
                Extract bill details from this text:
                $extractedText
                
                Return JSON with: shopName, totalAmount, category (Groceries, Dining, Shopping, Utilities, Travel, Other), items (name, price, quantity).
                Return ONLY the raw JSON.
            """.trimIndent()

            val response = localLLMService.generateResponse(prompt)
            
            try {
                val result = json.decodeFromString<ExtractedBill>(response)
                val bill = Bill(
                    name = result.shopName,
                    totalAmount = result.totalAmount,
                    category = result.category,
                    shopName = result.shopName
                )
                val items = result.items.map {
                    BillItem(billId = 0, name = it.name, price = it.price, quantity = it.quantity)
                }
                _extractionState.value = UiState.Success(BillData(bill, items))
            } catch (e: Exception) {
                _extractionState.value = UiState.Error("Local AI failed to parse the bill: ${e.message}")
            }
        }
    }

    fun saveBill(bill: Bill, items: List<BillItem>) {
        viewModelScope.launch {
            repository.saveBillWithItems(bill, items)
            _extractionState.value = UiState.Idle
        }
    }

    fun createHome(name: String) {
        viewModelScope.launch {
            repository.createHome(name)
        }
    }

    fun resetScanner() {
        _extractionState.value = UiState.Idle
    }

    val personalBills = repository.getPersonalBills().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val bankSenders = repository.getAllBankSenders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addBankSender(address: String, name: String) {
        viewModelScope.launch {
            repository.addBankSender(address, name)
        }
    }
}
