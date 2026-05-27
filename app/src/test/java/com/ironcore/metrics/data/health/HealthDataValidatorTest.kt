package com.ironcore.metrics.data.health

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HealthDataValidatorTest {

    private lateinit var validator: HealthDataValidator

    @Before
    fun setup() {
        validator = HealthDataValidator()
    }

    @Test
    fun `validateHeartRate returns heart rate when within range`() {
        assertEquals(72L, validator.validateHeartRate(72L))
        assertEquals(180L, validator.validateHeartRate(180L))
    }

    @Test
    fun `validateHeartRate returns 0 when heart rate is below minimum`() {
        assertEquals(0L, validator.validateHeartRate(25L))
    }

    @Test
    fun `validateHeartRate returns 0 when heart rate is above maximum`() {
        assertEquals(0L, validator.validateHeartRate(250L))
    }

    @Test
    fun `validateSteps caps steps at maximum daily threshold`() {
        assertEquals(100000L, validator.validateSteps(150000L))
    }

    @Test
    fun `validateSteps returns 0 when steps is negative`() {
        assertEquals(0L, validator.validateSteps(-100L))
    }

    @Test
    fun `validateRecoveryScore clamps score between 0 and 100`() {
        assertEquals(0, validator.validateRecoveryScore(-10))
        assertEquals(100, validator.validateRecoveryScore(110))
        assertEquals(50, validator.validateRecoveryScore(50))
    }

    @Test
    fun `validateHydration caps at maximum threshold`() {
        assertEquals(10000, validator.validateHydration(15000))
    }

    @Test
    fun `validateSpO2 returns 0 when below minimum`() {
        assertEquals(0.0, validator.validateSpO2(40.0), 0.1)
    }
}
