package com.ironcore.metrics.data.remote

import com.ironcore.metrics.data.remote.dto.MealPlanRequest
import com.ironcore.metrics.data.remote.dto.MealPlanResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface HomelabApiService {
    @POST("ai/generate-meal-plan")
    suspend fun generateMealPlan(@Body request: MealPlanRequest): MealPlanResponse
}
