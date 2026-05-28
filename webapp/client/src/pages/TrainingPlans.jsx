import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'
import {
  CalendarIcon,
  ClockIcon,
  FireIcon,
  TrophyIcon,
  ChartBarIcon,
  PlayIcon,
  PauseIcon,
  CheckCircleIcon,
  PlusIcon,
  AdjustmentsHorizontalIcon,
  BoltIcon,
  HeartIcon,
} from '@heroicons/react/24/outline'

const PROGRAM_TEMPLATES = [
  {
    id: 'beginner-strength',
    name: 'Beginner Strength Builder',
    duration: '8 weeks',
    level: 'Beginner',
    goal: 'Build Foundation',
    workoutsPerWeek: 3,
    description: 'Perfect for those new to strength training. Focus on form and building a solid foundation.',
    color: 'green',
  },
  {
    id: 'intermediate-hypertrophy',
    name: 'Muscle Growth Program',
    duration: '12 weeks',
    level: 'Intermediate',
    goal: 'Muscle Growth',
    workoutsPerWeek: 4,
    description: 'Hypertrophy-focused program with progressive overload for maximum muscle gains.',
    color: 'blue',
  },
  {
    id: 'advanced-powerlifting',
    name: 'Powerlifting Peaking',
    duration: '16 weeks',
    level: 'Advanced',
    goal: 'Strength',
    workoutsPerWeek: 5,
    description: 'Advanced program designed to peak your squat, bench, and deadlift for competition.',
    color: 'red',
  },
  {
    id: 'fat-loss',
    name: 'Fat Loss & Conditioning',
    duration: '10 weeks',
    level: 'All Levels',
    goal: 'Fat Loss',
    workoutsPerWeek: 5,
    description: 'High-intensity program combining strength and cardio for maximum fat loss.',
    color: 'orange',
  },
  {
    id: 'athletic-performance',
    name: 'Athletic Performance',
    duration: '12 weeks',
    level: 'Intermediate',
    goal: 'Performance',
    workoutsPerWeek: 4,
    description: 'Improve speed, power, and agility for sports performance.',
    color: 'purple',
  },
  {
    id: 'endurance',
    name: 'Endurance Builder',
    duration: '14 weeks',
    level: 'All Levels',
    goal: 'Endurance',
    workoutsPerWeek: 4,
    description: 'Build cardiovascular endurance and stamina for long-distance events.',
    color: 'cyan',
  },
]

const PERIODIZATION_PHASES = [
  { name: 'Anatomical Adaptation', weeks: 2, focus: 'Form & Technique', intensity: 'Low' },
  { name: 'Hypertrophy', weeks: 4, focus: 'Muscle Growth', intensity: 'Moderate' },
  { name: 'Strength', weeks: 3, focus: 'Max Strength', intensity: 'High' },
  { name: 'Power', weeks: 2, focus: 'Explosive Power', intensity: 'Very High' },
  { name: 'Peaking', weeks: 1, focus: 'Competition Prep', intensity: 'Max' },
]

export default function TrainingPlans() {
  const { token } = useAuthStore()
  const queryClient = useQueryClient()
  const [selectedProgram, setSelectedProgram] = useState(null)
  const [showCustomBuilder, setShowCustomBuilder] = useState(false)

  // Fetch active programs
  const { data: activePrograms } = useQuery({
    queryKey: ['active-programs'],
    queryFn: async () => {
      const response = await axios.get('/api/training/active', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.programs || []
    },
  })

  // Fetch program details
  const { data: programDetails } = useQuery({
    queryKey: ['program-details', selectedProgram],
    queryFn: async () => {
      if (!selectedProgram) return null
      const response = await axios.get(`/api/training/programs/${selectedProgram}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.program
    },
    enabled: !!selectedProgram,
  })

  // Start a program
  const startProgram = useMutation({
    mutationFn: async (programId) => {
      await axios.post(`/api/training/programs/${programId}/start`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['active-programs'])
      toast.success('Program started!')
    },
  })

  // Complete a workout
  const completeWorkout = useMutation({
    mutationFn: async ({ programId, workoutId }) => {
      await axios.post(`/api/training/programs/${programId}/workouts/${workoutId}/complete`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['active-programs'])
      queryClient.invalidateQueries(['program-details'])
      toast.success('Workout completed!')
    },
  })

  return (
    <div className="min-h-screen bg-ironcore-darker p-6">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-white">Training Plans & Programs</h1>
            <p className="text-gray-400 mt-1">Structured programs for progressive results</p>
          </div>
          <button
            onClick={() => setShowCustomBuilder(true)}
            className="btn-primary flex items-center gap-2"
          >
            <PlusIcon className="w-5 h-5" />
            Create Custom Program
          </button>
        </div>

        {/* Active Programs */}
        {activePrograms && activePrograms.length > 0 && (
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Your Active Programs</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {activePrograms.map((program) => (
                <div key={program.id} className="bg-ironcore-dark rounded-lg p-4 border-2 border-primary-500">
                  <div className="flex justify-between items-start mb-3">
                    <div>
                      <h3 className="text-white font-semibold text-lg">{program.name}</h3>
                      <p className="text-gray-400 text-sm">Week {program.currentWeek} of {program.totalWeeks}</p>
                    </div>
                    <span className="px-3 py-1 bg-green-500/20 text-green-400 text-xs font-semibold rounded-full">
                      Active
                    </span>
                  </div>

                  <div className="mb-3">
                    <div className="flex justify-between text-sm mb-1">
                      <span className="text-gray-400">Progress</span>
                      <span className="text-white font-semibold">{program.completedWorkouts} / {program.totalWorkouts} workouts</span>
                    </div>
                    <div className="w-full bg-gray-700 rounded-full h-2">
                      <div
                        className="bg-gradient-to-r from-blue-500 to-purple-500 h-2 rounded-full transition-all"
                        style={{ width: `${(program.completedWorkouts / program.totalWorkouts) * 100}%` }}
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-3 gap-2 mb-3">
                    <div className="bg-gray-800 rounded p-2 text-center">
                      <p className="text-gray-400 text-xs">Next Workout</p>
                      <p className="text-white font-semibold text-sm">{program.nextWorkout}</p>
                    </div>
                    <div className="bg-gray-800 rounded p-2 text-center">
                      <p className="text-gray-400 text-xs">Adherence</p>
                      <p className="text-green-400 font-semibold text-sm">{program.adherence}%</p>
                    </div>
                    <div className="bg-gray-800 rounded p-2 text-center">
                      <p className="text-gray-400 text-xs">Days Left</p>
                      <p className="text-white font-semibold text-sm">{program.daysRemaining}</p>
                    </div>
                  </div>

                  <button
                    onClick={() => setSelectedProgram(program.id)}
                    className="w-full px-4 py-2 bg-primary-500 hover:bg-primary-600 text-white font-semibold rounded-lg transition-colors"
                  >
                    View Details
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Program Templates */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Program Templates</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {PROGRAM_TEMPLATES.map((program) => (
              <div
                key={program.id}
                className="bg-ironcore-dark rounded-lg p-4 border-2 border-gray-700 hover:border-primary-500 transition-all cursor-pointer"
                onClick={() => setSelectedProgram(program.id)}
              >
                <div className="flex justify-between items-start mb-3">
                  <div className={`w-12 h-12 bg-${program.color}-500/20 rounded-lg flex items-center justify-center`}>
                    <TrophyIcon className={`w-6 h-6 text-${program.color}-400`} />
                  </div>
                  <span className={`px-3 py-1 bg-${program.color}-500/20 text-${program.color}-400 text-xs font-semibold rounded-full`}>
                    {program.level}
                  </span>
                </div>

                <h3 className="text-white font-semibold text-lg mb-2">{program.name}</h3>
                <p className="text-gray-400 text-sm mb-4">{program.description}</p>

                <div className="space-y-2 mb-4">
                  <div className="flex items-center gap-2 text-sm">
                    <ClockIcon className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-300">{program.duration}</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm">
                    <CalendarIcon className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-300">{program.workoutsPerWeek}x per week</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm">
                    <TrophyIcon className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-300">Goal: {program.goal}</span>
                  </div>
                </div>

                <button
                  onClick={(e) => {
                    e.stopPropagation()
                    startProgram.mutate(program.id)
                  }}
                  className="w-full px-4 py-2 bg-primary-500 hover:bg-primary-600 text-white font-semibold rounded-lg transition-colors flex items-center justify-center gap-2"
                >
                  <PlayIcon className="w-4 h-4" />
                  Start Program
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Periodization Overview */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Understanding Periodization</h2>
          <p className="text-gray-400 mb-6">
            Our programs use periodization to systematically vary training intensity and volume for optimal results.
          </p>
          
          <div className="space-y-3">
            {PERIODIZATION_PHASES.map((phase, idx) => (
              <div key={idx} className="bg-ironcore-dark rounded-lg p-4">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-primary-500/20 rounded-full flex items-center justify-center text-primary-400 font-bold">
                      {idx + 1}
                    </div>
                    <div>
                      <h4 className="text-white font-semibold">{phase.name}</h4>
                      <p className="text-gray-400 text-sm">{phase.weeks} weeks</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-gray-400 text-sm">Focus</p>
                    <p className="text-white font-semibold">{phase.focus}</p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <span className="text-gray-400 text-sm">Intensity:</span>
                  <div className="flex-1 bg-gray-700 rounded-full h-2">
                    <div
                      className={`h-2 rounded-full ${
                        phase.intensity === 'Low' ? 'bg-green-500 w-1/5' :
                        phase.intensity === 'Moderate' ? 'bg-blue-500 w-2/5' :
                        phase.intensity === 'High' ? 'bg-orange-500 w-3/5' :
                        phase.intensity === 'Very High' ? 'bg-red-500 w-4/5' :
                        'bg-purple-500 w-full'
                      }`}
                    />
                  </div>
                  <span className="text-white text-sm font-semibold">{phase.intensity}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Progressive Overload Tracker */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Progressive Overload Tracking</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-ironcore-dark rounded-lg p-4">
              <div className="flex items-center gap-3 mb-3">
                <div className="p-3 bg-blue-500/20 rounded-lg">
                  <ChartBarIcon className="w-6 h-6 text-blue-400" />
                </div>
                <div>
                  <p className="text-gray-400 text-sm">Volume Load</p>
                  <p className="text-2xl font-bold text-white">125,450 kg</p>
                </div>
              </div>
              <p className="text-green-400 text-sm">+12% from last week</p>
            </div>

            <div className="bg-ironcore-dark rounded-lg p-4">
              <div className="flex items-center gap-3 mb-3">
                <div className="p-3 bg-orange-500/20 rounded-lg">
                  <BoltIcon className="w-6 h-6 text-orange-400" />
                </div>
                <div>
                  <p className="text-gray-400 text-sm">Avg Intensity</p>
                  <p className="text-2xl font-bold text-white">78%</p>
                </div>
              </div>
              <p className="text-green-400 text-sm">+5% from last week</p>
            </div>

            <div className="bg-ironcore-dark rounded-lg p-4">
              <div className="flex items-center gap-3 mb-3">
                <div className="p-3 bg-purple-500/20 rounded-lg">
                  <HeartIcon className="w-6 h-6 text-purple-400" />
                </div>
                <div>
                  <p className="text-gray-400 text-sm">Recovery Score</p>
                  <p className="text-2xl font-bold text-white">86/100</p>
                </div>
              </div>
              <p className="text-gray-400 text-sm">Ready for training</p>
            </div>
          </div>
        </div>

        {/* Training Principles */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="card">
            <h3 className="text-lg font-semibold text-white mb-4">Key Training Principles</h3>
            <div className="space-y-3">
              <div className="flex items-start gap-3">
                <CheckCircleIcon className="w-5 h-5 text-green-400 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-white font-semibold">Progressive Overload</p>
                  <p className="text-gray-400 text-sm">Gradually increase weight, reps, or sets over time</p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <CheckCircleIcon className="w-5 h-5 text-green-400 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-white font-semibold">Specificity</p>
                  <p className="text-gray-400 text-sm">Train movements and energy systems specific to your goals</p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <CheckCircleIcon className="w-5 h-5 text-green-400 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-white font-semibold">Recovery</p>
                  <p className="text-gray-400 text-sm">Allow adequate rest between sessions for adaptation</p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <CheckCircleIcon className="w-5 h-5 text-green-400 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-white font-semibold">Variation</p>
                  <p className="text-gray-400 text-sm">Periodically change exercises and rep ranges</p>
                </div>
              </div>
            </div>
          </div>

          <div className="card">
            <h3 className="text-lg font-semibold text-white mb-4">Program Customization</h3>
            <div className="space-y-3">
              <div className="flex items-center justify-between p-3 bg-ironcore-dark rounded-lg">
                <span className="text-white">Training Frequency</span>
                <select className="bg-gray-800 text-white px-3 py-1 rounded border border-gray-700">
                  <option>3x per week</option>
                  <option>4x per week</option>
                  <option>5x per week</option>
                  <option>6x per week</option>
                </select>
              </div>
              <div className="flex items-center justify-between p-3 bg-ironcore-dark rounded-lg">
                <span className="text-white">Session Duration</span>
                <select className="bg-gray-800 text-white px-3 py-1 rounded border border-gray-700">
                  <option>45 minutes</option>
                  <option>60 minutes</option>
                  <option>75 minutes</option>
                  <option>90 minutes</option>
                </select>
              </div>
              <div className="flex items-center justify-between p-3 bg-ironcore-dark rounded-lg">
                <span className="text-white">Experience Level</span>
                <select className="bg-gray-800 text-white px-3 py-1 rounded border border-gray-700">
                  <option>Beginner</option>
                  <option>Intermediate</option>
                  <option>Advanced</option>
                  <option>Elite</option>
                </select>
              </div>
              <button className="w-full px-4 py-2 bg-primary-500 hover:bg-primary-600 text-white font-semibold rounded-lg transition-colors flex items-center justify-center gap-2">
                <AdjustmentsHorizontalIcon className="w-5 h-5" />
                Apply Customization
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
