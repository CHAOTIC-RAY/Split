package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ai.LocalLLMService
import com.example.ai.OCRService
import com.example.data.BillRepository

class BillViewModelFactory(
    private val repository: BillRepository,
    private val ocrService: OCRService,
    private val localLLMService: LocalLLMService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillViewModel(repository, ocrService, localLLMService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
