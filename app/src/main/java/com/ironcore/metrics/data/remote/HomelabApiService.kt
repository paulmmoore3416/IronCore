package com.ironcore.metrics.data.remote

import com.ironcore.metrics.data.remote.dto.OllamaGenerateRequest
import com.ironcore.metrics.data.remote.dto.OllamaGenerateResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface HomelabApiService {
    @POST("api/generate")
    suspend fun generateWithGranite(@Body request: OllamaGenerateRequest): OllamaGenerateResponse
}
