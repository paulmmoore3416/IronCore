package com.ironcore.metrics.domain

import com.ironcore.metrics.data.remote.HomelabApiService
import com.ironcore.metrics.data.remote.dto.OllamaGenerateResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class WorkoutPlanServiceTest {

    @Mock
    private lateinit var homelabApiService: HomelabApiService

    private lateinit var workoutPlanService: WorkoutPlanService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        workoutPlanService = WorkoutPlanService(homelabApiService)
    }

    @Test
    fun `generateWorkoutPlan returns plan when API succeeds`() = runBlocking {
        val mockResponse = """
            {
              "planName": "Iron Core Strength",
              "exercises": [
                { "name": "Squat", "sets": 5, "reps": "5", "intensity": "85% 1RM" }
              ],
              "coachingTip": "Focus on depth."
            }
        """.trimIndent()

        `when`(homelabApiService.generateWithGranite(any())).thenReturn(
            OllamaGenerateResponse(model = "test", created_at = "", response = mockResponse, done = true, total_duration = 0L)
        )

        val plan = workoutPlanService.generateWorkoutPlan(85, "Strength")

        assertNotNull(plan)
        assertEquals("Iron Core Strength", plan?.planName)
        assertEquals(1, plan?.exercises?.size)
        assertEquals("Squat", plan?.exercises?.first()?.name)
    }
}
