package com.ironcore.metrics.ui.workout

import com.ironcore.metrics.data.local.entities.Workout
import com.ironcore.metrics.data.local.entities.WorkoutSet
import com.ironcore.metrics.domain.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicLong

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: WorkoutViewModel
    private lateinit var fakeRepository: WorkoutRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = object : WorkoutRepository {
            val idCounter = AtomicLong(1)
            override fun getAllWorkouts(): Flow<List<Workout>> = flowOf(emptyList())
            override suspend fun saveWorkout(workout: Workout): Long = idCounter.getAndIncrement()
            override suspend fun saveWorkoutSet(workoutSet: WorkoutSet) {}
            override fun getSetsForWorkout(workoutId: Long): Flow<List<WorkoutSet>> = flowOf(emptyList())
        }
        viewModel = WorkoutViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startWorkout updates currentWorkout state`() {
        viewModel.startWorkout("Test Lift")
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.currentWorkout.value?.name == "Test Lift")
        assert(viewModel.currentWorkout.value?.id == 1L)
    }
}
