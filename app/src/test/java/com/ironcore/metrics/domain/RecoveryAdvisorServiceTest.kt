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

class RecoveryAdvisorServiceTest {

    @Mock
    private lateinit var homelabApiService: HomelabApiService

    private lateinit var recoveryAdvisorService: RecoveryAdvisorService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        recoveryAdvisorService = RecoveryAdvisorService(homelabApiService)
    }

    @Test
    fun `getRecoveryAdvice returns advice when API succeeds`() = runBlocking {
        val mockResponse = """
            {
              "advice": "Great job! Keep it up.",
              "priority": "low",
              "recommendations": ["Rest well", "Drink water"]
            }
        """.trimIndent()

        `when`(homelabApiService.generateWithGranite(any())).thenReturn(
            OllamaGenerateResponse(model = "test", created_at = "", response = mockResponse, done = true, total_duration = 0L)
        )

        val advice = recoveryAdvisorService.getRecoveryAdvice(10000, 70, 2000, 500.0, 2000, 85)

        assertNotNull(advice)
        assertEquals("Great job! Keep it up.", advice?.advice)
        assertEquals("low", advice?.priority)
        assertEquals(2, advice?.recommendations?.size)
    }

    @Test
    fun `getRecoveryAdvice extracts JSON from markdown blocks`() = runBlocking {
        val mockResponse = """
            Here is your advice:
            ```json
            {
              "advice": "Extracted advice",
              "priority": "medium",
              "recommendations": ["Rec 1"]
            }
            ```
            Hope this helps!
        """.trimIndent()

        `when`(homelabApiService.generateWithGranite(any())).thenReturn(
            OllamaGenerateResponse(model = "test", created_at = "", response = mockResponse, done = true, total_duration = 0L)
        )

        val advice = recoveryAdvisorService.getRecoveryAdvice(5000, 80, 1000, 300.0, 1500, 60)

        assertNotNull(advice)
        assertEquals("Extracted advice", advice?.advice)
    }

    @Test
    fun `getRecoveryAdvice returns fallback on API error`() = runBlocking {
        `when`(homelabApiService.generateWithGranite(any())).thenThrow(RuntimeException("API Error"))

        val advice = recoveryAdvisorService.getRecoveryAdvice(1000, 100, 500, 100.0, 1000, 20)

        assertNotNull(advice)
        assertEquals("high", advice?.priority) // Fallback for score 20
    }
}
