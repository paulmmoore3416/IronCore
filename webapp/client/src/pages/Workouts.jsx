import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'
import {
  FireIcon,
  HeartIcon,
  ClockIcon,
  ChartBarIcon,
  PlayIcon,
  PauseIcon,
  StopIcon,
  PlusIcon,
  CalendarIcon,
  TrophyIcon,
  BoltIcon,
  ArrowTrendingUpIcon,
} from '@heroicons/react/24/outline'

const WORKOUT_MODALITIES = [
  { id: 'weight-training', name: 'Weight Training', icon: '🏋️', color: 'bg-blue-500', description: 'Strength & Hypertrophy' },
  { id: 'calisthenics', name: 'Calisthenics', icon: '🤸', color: 'bg-orange-500', description: 'Bodyweight Mastery' },
  { id: 'running', name: 'Running', icon: '🏃', color: 'bg-cyan-500', description: 'Cardio Endurance' },
  { id: 'cycling', name: 'Cycling', icon: '🚴', color: 'bg-yellow-500', description: 'Low Impact Cardio' },
  { id: 'swimming', name: 'Swimming', icon: '🏊', color: 'bg-blue-400', description: 'Full Body Cardio' },
  { id: 'hiking', name: 'Hiking', icon: '🥾', color: 'bg-green-500', description: 'Outdoor Adventure' },
  { id: 'yoga', name: 'Yoga', icon: '🧘', color: 'bg-purple-500', description: 'Flexibility & Balance' },
  { id: 'boxing', name: 'Boxing', icon: '🥊', color: 'bg-red-500', description: 'Combat Training' },
  { id: 'crossfit', name: 'CrossFit', icon: '⚡', color: 'bg-indigo-500', description: 'High Intensity' },
  { id: 'pilates', name: 'Pilates', icon: '🤸‍♀️', color: 'bg-pink-500', description: 'Core Strength' },
  { id: 'rowing', name: 'Rowing', icon: '🚣', color: 'bg-teal-500', description: 'Full Body Power' },
  { id: 'martial-arts', name: 'Martial Arts', icon: '🥋', color: 'bg-gray-700', description: 'Discipline & Technique' },
]

const MUSCLE_GROUPS = [
  { id: 'chest', name: 'Chest', icon: '💪', exercises: ['Bench Press', 'Push-ups', 'Dumbbell Flyes', 'Cable Crossovers'] },
  { id: 'back', name: 'Back', icon: '🦾', exercises: ['Pull-ups', 'Deadlifts', 'Rows', 'Lat Pulldowns'] },
  { id: 'shoulders', name: 'Shoulders', icon: '🏋️', exercises: ['Overhead Press', 'Lateral Raises', 'Front Raises', 'Shrugs'] },
  { id: 'arms', name: 'Arms', icon: '💪', exercises: ['Bicep Curls', 'Tricep Dips', 'Hammer Curls', 'Skull Crushers'] },
  { id: 'legs', name: 'Legs', icon: '🦵', exercises: ['Squats', 'Lunges', 'Leg Press', 'Calf Raises'] },
  { id: 'abs', name: 'Abs', icon: '🎯', exercises: ['Crunches', 'Planks', 'Russian Twists', 'Leg Raises'] },
]

export default function Workouts() {
  const { token } = useAuthStore()
  const queryClient = useQueryClient()
  const [selectedModality, setSelectedModality] = useState(null)
  const [selectedMuscleGroup, setSelectedMuscleGroup] = useState(null)
  const [isWorkoutActive, setIsWorkoutActive] = useState(false)
  const [workoutTimer, setWorkoutTimer] = useState(0)
  const [isPaused, setIsPaused] = useState(false)
  const [showCreateWorkout, setShowCreateWorkout] = useState(false)

  // Fetch recent workouts
  const { data: workouts, isLoading } = useQuery({
    queryKey: ['workouts'],
    queryFn: async () => {
      const response = await axios.get('/api/workouts', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.workouts || []
    },
  })

  // Fetch workout stats
  const { data: stats } = useQuery({
    queryKey: ['workout-stats'],
    queryFn: async () => {
      const response = await axios.get('/api/workouts/stats', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.stats || {
        totalWorkouts: 0,
        totalDuration: 0,
        totalCalories: 0,
        weeklyStreak: 0,
        favoriteModality: 'None',
      }
    },
  })

  // Start workout timer
  const startWorkout = () => {
    setIsWorkoutActive(true)
    setIsPaused(false)
    const interval = setInterval(() => {
      setWorkoutTimer((prev) => prev + 1)
    }, 1000)
    return () => clearInterval(interval)
  }

  const formatTime = (seconds) => {
    const hrs = Math.floor(seconds / 3600)
    const mins = Math.floor((seconds % 3600) / 60)
    const secs = seconds % 60
    return `${hrs.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }

  return (
    <div className="min-h-screen bg-ironcore-darker p-6">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-white">Training Command Center</h1>
            <p className="text-gray-400 mt-1">Your centralized dashboard for all physical modalities</p>
          </div>
          <button
            onClick={() => setShowCreateWorkout(true)}
            className="btn-primary flex items-center gap-2"
          >
            <PlusIcon className="w-5 h-5" />
            New Workout
          </button>
        </div>

        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-blue-500/20 rounded-lg">
                <TrophyIcon className="w-6 h-6 text-blue-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Total Workouts</p>
                <p className="text-2xl font-bold text-white">{stats?.totalWorkouts || 0}</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-orange-500/20 rounded-lg">
                <ClockIcon className="w-6 h-6 text-orange-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Total Time</p>
                <p className="text-2xl font-bold text-white">{Math.floor((stats?.totalDuration || 0) / 60)}h</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-red-500/20 rounded-lg">
                <FireIcon className="w-6 h-6 text-red-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Calories Burned</p>
                <p className="text-2xl font-bold text-white">{(stats?.totalCalories || 0).toLocaleString()}</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-green-500/20 rounded-lg">
                <BoltIcon className="w-6 h-6 text-green-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Weekly Streak</p>
                <p className="text-2xl font-bold text-white">{stats?.weeklyStreak || 0} days</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-purple-500/20 rounded-lg">
                <ArrowTrendingUpIcon className="w-6 h-6 text-purple-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Favorite</p>
                <p className="text-lg font-bold text-white">{stats?.favoriteModality || 'None'}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Active Workout Timer */}
        {isWorkoutActive && (
          <div className="card bg-gradient-to-r from-blue-600 to-purple-600">
            <div className="flex justify-between items-center">
              <div>
                <h3 className="text-xl font-bold text-white mb-2">Workout in Progress</h3>
                <p className="text-3xl font-mono text-white">{formatTime(workoutTimer)}</p>
              </div>
              <div className="flex gap-3">
                <button
                  onClick={() => setIsPaused(!isPaused)}
                  className="p-4 bg-white/20 hover:bg-white/30 rounded-full transition-colors"
                >
                  {isPaused ? (
                    <PlayIcon className="w-6 h-6 text-white" />
                  ) : (
                    <PauseIcon className="w-6 h-6 text-white" />
                  )}
                </button>
                <button
                  onClick={() => {
                    setIsWorkoutActive(false)
                    setWorkoutTimer(0)
                    toast.success('Workout completed!')
                  }}
                  className="p-4 bg-red-500/20 hover:bg-red-500/30 rounded-full transition-colors"
                >
                  <StopIcon className="w-6 h-6 text-white" />
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Workout Modalities */}
        <div className="card">
          <h2 className="text-2xl font-bold text-white mb-4">Specialized Modalities</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
            {WORKOUT_MODALITIES.map((modality) => (
              <button
                key={modality.id}
                onClick={() => setSelectedModality(modality)}
                className={`p-4 rounded-xl border-2 transition-all hover:scale-105 ${
                  selectedModality?.id === modality.id
                    ? 'border-primary-500 bg-primary-500/10'
                    : 'border-gray-700 bg-ironcore-dark hover:border-gray-600'
                }`}
              >
                <div className="text-4xl mb-2">{modality.icon}</div>
                <h3 className="text-white font-semibold text-sm">{modality.name}</h3>
                <p className="text-gray-400 text-xs mt-1">{modality.description}</p>
              </button>
            ))}
          </div>
        </div>

        {/* Muscle Group Focus */}
        <div className="card">
          <h2 className="text-2xl font-bold text-white mb-4">Muscle Group Focus</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {MUSCLE_GROUPS.map((group) => (
              <button
                key={group.id}
                onClick={() => setSelectedMuscleGroup(group)}
                className={`p-4 rounded-xl border-2 transition-all hover:scale-105 ${
                  selectedMuscleGroup?.id === group.id
                    ? 'border-orange-500 bg-orange-500/10'
                    : 'border-gray-700 bg-ironcore-dark hover:border-gray-600'
                }`}
              >
                <div className="text-3xl mb-2">{group.icon}</div>
                <h3 className="text-white font-semibold">{group.name}</h3>
              </button>
            ))}
          </div>

          {selectedMuscleGroup && (
            <div className="mt-6 p-4 bg-ironcore-dark rounded-lg">
              <h3 className="text-lg font-bold text-white mb-3">
                {selectedMuscleGroup.name} Exercises
              </h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {selectedMuscleGroup.exercises.map((exercise, idx) => (
                  <div
                    key={idx}
                    className="p-3 bg-gray-800 rounded-lg hover:bg-gray-700 transition-colors cursor-pointer"
                  >
                    <p className="text-white text-sm">{exercise}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Recent Workouts */}
        <div className="card">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-2xl font-bold text-white">Recent Workouts</h2>
            <button className="text-primary-500 hover:text-primary-400 text-sm font-medium">
              View All
            </button>
          </div>

          {isLoading ? (
            <div className="flex justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500"></div>
            </div>
          ) : workouts && workouts.length > 0 ? (
            <div className="space-y-3">
              {workouts.slice(0, 5).map((workout, idx) => (
                <div
                  key={idx}
                  className="p-4 bg-ironcore-dark rounded-lg hover:bg-gray-800 transition-colors cursor-pointer"
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 className="text-white font-semibold">{workout.name || 'Workout'}</h3>
                      <p className="text-gray-400 text-sm mt-1">
                        {workout.type || 'General'} • {workout.duration || 0} min
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-primary-500 font-semibold">{workout.calories || 0} cal</p>
                      <p className="text-gray-400 text-sm">{new Date(workout.date).toLocaleDateString()}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <FireIcon className="w-16 h-16 text-gray-600 mx-auto mb-4" />
              <p className="text-gray-400">No workouts yet. Start your first workout!</p>
              <button
                onClick={startWorkout}
                className="btn-primary mt-4"
              >
                Start Workout
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}