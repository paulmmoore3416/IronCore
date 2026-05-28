package com.ironcore.metrics.data.remote.dto

data class RemoteWorkoutRoutine(
    val modality: String,
    val days: List<RemoteWorkoutDay>
)

data class RemoteWorkoutDay(
    val dayNumber: Int,
    val focus: String,
    val exercises: MutableList<RemoteExercise>
)

data class RemoteExercise(
    var name: String,
    var sets: Int,
    var reps: String,
    var rest: String,
    var weight: String = ""
)