package com.example.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class LocalLLMService(private val context: Context) {
    private var llmInference: LlmInference? = null
    
    // Placeholder URL for a LiteRT LLM model (e.g. Gemma 2b)
    // In a real app, this would be a verified Google/Kaggle URL.
    private val modelUrl = "https://example.com/gemma-2b-it-cpu-int4.bin" 
    private val modelFileName = "llm_model.bin"

    val modelFile: File by lazy {
        File(context.filesDir, modelFileName)
    }

    fun isModelDownloaded(): Boolean = modelFile.exists()

    suspend fun downloadModel(onProgress: (Float) -> Unit): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL(modelUrl)
            val connection = url.openConnection()
            connection.connect()

            val fileLength = connection.contentLength
            val inputStream = url.openStream()
            val outputStream = modelFile.outputStream()

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                totalBytesRead += bytesRead
                outputStream.write(buffer, 0, bytesRead)
                if (fileLength > 0) {
                    onProgress(totalBytesRead.toFloat() / fileLength.toFloat())
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun initialize() {
        if (!isModelDownloaded()) return
        
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelFile.absolutePath)
            .setMaxTokens(512)
            .setTopK(40)
            .setTemperature(0.7f)
            .build()
            
        llmInference = LlmInference.createFromOptions(context, options)
    }

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        if (llmInference == null) {
            initialize()
        }
        llmInference?.generateResponse(prompt) ?: "Model not initialized or downloaded."
    }

    fun close() {
        llmInference?.close()
    }
}
