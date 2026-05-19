package com.ironcore.metrics.data.remote.dto

data class OllamaGenerateRequest(
    val model: String = "granite-code:3b", // Replace with your exact Granite model name
    val prompt: String,
    val stream: Boolean = false,
    val format: String = "json"
)

data class OllamaGenerateResponse(
    val model: String,
    val created_at: String,
    val response: String,
    val done: Boolean,
    val total_duration: Long
)
