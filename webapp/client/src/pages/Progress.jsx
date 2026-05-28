import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'
import {
  TrophyIcon,
  ChartBarIcon,
  FireIcon,
  BoltIcon,
  FlagIcon,
  CheckCircleIcon,
  ClockIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  PlusIcon,
  StarIcon,
} from '@heroicons/react/24/outline'
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts'

const GOAL_CATEGORIES = [
  { id: 'strength', name: 'Strength', icon: '💪', color: 'red' },
  { id: 'endurance', name: 'Endurance', icon: '🏃', color: 'blue' },
  { id: 'weight', name: 'Weight Loss', icon: '⚖️', color: 'green' },
  { id: 'muscle', name: 'Muscle Gain', icon: '🦾', color: 'purple' },
  { id: 'performance', name: 'Performance', icon: '⚡', color: 'yellow' },
  { id: 'health', name: 'Health', icon: '❤️', color: 'pink' },
]

export default function Progress() {
  const { token } = useAuthStore()
  const queryClient = useQueryClient()
  const [selectedPeriod, setSelectedPeriod] = useState('30d')
  const [showAddGoal, setShowAddGoal] = useState(false)

  // Fetch goals
  const { data: goals } = useQuery({
    queryKey: ['goals'],
    queryFn: async () => {
      const response = await axios.get('/api/progress/goals', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.goals || []
    },
  })

  // Fetch personal records
  const { data: personalRecords } = useQuery({
    queryKey: ['personal-records'],
    queryFn: async () => {
      const response = await axios.get('/api/progress/personal-records', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.records || []
    },
  })

  // Fetch milestones
  const { data: milestones } = useQuery({
    queryKey: ['milestones'],
    queryFn: async () => {
      const response = await axios.get('/api/progress/milestones', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.milestones || []
    },
  })

  // Fetch progress trends
  const { data: progressTrends } = useQuery({
    queryKey: ['progress-trends', selectedPeriod],
    queryFn: async () => {
      const response = await axios.get(`/api/progress/trends?period=${selectedPeriod}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.trends || []
    },
  })

  // Mock strength progress data
  const strengthProgress = [
    { month: 'Jan', squat: 225, bench: 185, deadlift: 315 },
    { month: 'Feb', squat: 235, bench: 195, deadlift: 335 },
    { month: 'Mar', squat: 245, bench: 205, deadlift: 355 },
    { month: 'Apr', squat: 255, bench: 215, deadlift: 375 },
    { month: 'May', squat: 275, bench: 225, deadlift: 405 },
  ]

  // Mock body composition data
  const bodyCompProgress = [
    { month: 'Jan', weight: 185, bodyFat: 18, muscle: 145 },
    { month: 'Feb', weight: 183, bodyFat: 17, muscle: 147 },
    { month: 'Mar', weight: 181, bodyFat: 16, muscle: 149 },
    { month: 'Apr', weight: 180, bodyFat: 15.5, muscle: 151 },
    { month: 'May', weight: 178, bodyFat: 15, muscle: 152 },
  ]

  return (
    <div className="min-h-screen bg-ironcore-darker p-6">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-white">Progress & Goals</h1>
            <p className="text-gray-400 mt-1">Track your journey and celebrate achievements</p>
          </div>
          <button
            onClick={() => setShowAddGoal(true)}
            className="btn-primary flex items-center gap-2"
          >
            <PlusIcon className="w-5 h-5" />
            Add Goal
          </button>
        </div>

        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="card bg-gradient-to-br from-yellow-600 to-yellow-800">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-yellow-100 text-sm mb-1">Total PRs</p>
                <p className="text-4xl font-bold text-white">{personalRecords?.length || 0}</p>
                <p className="text-yellow-200 text-xs mt-1">Personal Records</p>
              </div>
              <TrophyIcon className="w-16 h-16 text-yellow-300 opacity-50" />
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-green-500/20 rounded-lg">
                <CheckCircleIcon className="w-6 h-6 text-green-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Goals Achieved</p>
                <p className="text-2xl font-bold text-white">{goals?.filter(g => g.completed).length || 0}</p>
                <p className="text-gray-400 text-xs">This year</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-blue-500/20 rounded-lg">
                <FlagIcon className="w-6 h-6 text-blue-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Active Goals</p>
                <p className="text-2xl font-bold text-white">{goals?.filter(g => !g.completed).length || 0}</p>
                <p className="text-gray-400 text-xs">In progress</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-purple-500/20 rounded-lg">
                <StarIcon className="w-6 h-6 text-purple-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Milestones</p>
                <p className="text-2xl font-bold text-white">{milestones?.length || 0}</p>
                <p className="text-green-400 text-xs">+2 this month</p>
              </div>
            </div>
          </div>
        </div>

        {/* Active Goals */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Active Goals</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {goals && goals.filter(g => !g.completed).map((goal) => (
              <div key={goal.id} className="bg-ironcore-dark rounded-lg p-4 border-2 border-gray-700 hover:border-primary-500 transition-colors">
                <div className="flex items-start justify-between mb-3">
                  <div className="flex items-center gap-3">
                    <div className="text-4xl">{GOAL_CATEGORIES.find(c => c.id === goal.category)?.icon || '🎯'}</div>
                    <div>
                      <h3 className="text-white font-semibold">{goal.title}</h3>
                      <p className="text-gray-400 text-sm">{goal.category}</p>
                    </div>
                  </div>
                  <span className="px-3 py-1 bg-blue-500/20 text-blue-400 text-xs font-semibold rounded-full">
                    Active
                  </span>
                </div>

                <p className="text-gray-300 text-sm mb-3">{goal.description}</p>

                <div className="mb-3">
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-400">Progress</span>
                    <span className="text-white font-semibold">{goal.current} / {goal.target} {goal.unit}</span>
                  </div>
                  <div className="w-full bg-gray-700 rounded-full h-2">
                    <div
                      className="bg-gradient-to-r from-blue-500 to-purple-500 h-2 rounded-full transition-all"
                      style={{ width: `${(goal.current / goal.target) * 100}%` }}
                    />
                  </div>
                </div>

                <div className="flex items-center justify-between text-sm">
                  <div className="flex items-center gap-2 text-gray-400">
                    <ClockIcon className="w-4 h-4" />
                    <span>{goal.daysRemaining} days left</span>
                  </div>
                  <div className="flex items-center gap-2 text-green-400">
                    <ArrowTrendingUpIcon className="w-4 h-4" />
                    <span>{goal.percentComplete}% complete</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Charts Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Strength Progress */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Strength Progress (Big 3)</h2>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={strengthProgress}>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="month" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
                  labelStyle={{ color: '#fff' }}
                />
                <Legend />
                <Line type="monotone" dataKey="squat" stroke="#3b82f6" strokeWidth={2} name="Squat (lbs)" />
                <Line type="monotone" dataKey="bench" stroke="#10b981" strokeWidth={2} name="Bench (lbs)" />
                <Line type="monotone" dataKey="deadlift" stroke="#f59e0b" strokeWidth={2} name="Deadlift (lbs)" />
              </LineChart>
            </ResponsiveContainer>
          </div>

          {/* Body Composition */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Body Composition Trends</h2>
            <ResponsiveContainer width="100%" height={300}>
              <AreaChart data={bodyCompProgress}>
                <defs>
                  <linearGradient id="colorWeight" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#10b981" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#10b981" stopOpacity={0}/>
                  </linearGradient>
                  <linearGradient id="colorFat" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#ef4444" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#ef4444" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="month" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
                  labelStyle={{ color: '#fff' }}
                />
                <Legend />
                <Area type="monotone" dataKey="weight" stroke="#10b981" fillOpacity={1} fill="url(#colorWeight)" name="Weight (lbs)" />
                <Area type="monotone" dataKey="bodyFat" stroke="#ef4444" fillOpacity={1} fill="url(#colorFat)" name="Body Fat %" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Personal Records */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Personal Records</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {personalRecords && personalRecords.slice(0, 6).map((record, idx) => (
              <div key={idx} className="bg-ironcore-dark rounded-lg p-4">
                <div className="flex items-center gap-3 mb-2">
                  <div className="p-2 bg-yellow-500/20 rounded-lg">
                    <TrophyIcon className="w-5 h-5 text-yellow-400" />
                  </div>
                  <div className="flex-1">
                    <h3 className="text-white font-semibold">{record.exercise}</h3>
                    <p className="text-gray-400 text-xs">{record.date}</p>
                  </div>
                </div>
                <div className="flex items-baseline gap-2">
                  <span className="text-3xl font-bold text-white">{record.value}</span>
                  <span className="text-gray-400">{record.unit}</span>
                </div>
                <div className="flex items-center gap-1 text-green-400 text-sm mt-2">
                  <ArrowTrendingUpIcon className="w-4 h-4" />
                  <span>+{record.improvement} from previous</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Milestones */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Recent Milestones</h2>
          <div className="space-y-3">
            {milestones && milestones.map((milestone, idx) => (
              <div key={idx} className="bg-ironcore-dark rounded-lg p-4 flex items-center gap-4">
                <div className="p-3 bg-purple-500/20 rounded-full">
                  <StarIcon className="w-6 h-6 text-purple-400" />
                </div>
                <div className="flex-1">
                  <h3 className="text-white font-semibold">{milestone.title}</h3>
                  <p className="text-gray-400 text-sm">{milestone.description}</p>
                </div>
                <div className="text-right">
                  <p className="text-gray-400 text-sm">{milestone.date}</p>
                  <span className="px-3 py-1 bg-green-500/20 text-green-400 text-xs font-semibold rounded-full">
                    Achieved
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Goal Categories */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Set New Goals</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
            {GOAL_CATEGORIES.map((category) => (
              <button
                key={category.id}
                className="p-4 rounded-xl border-2 border-gray-700 bg-ironcore-dark hover:border-primary-500 transition-all hover:scale-105"
              >
                <div className="text-4xl mb-2">{category.icon}</div>
                <p className="text-white font-semibold text-sm">{category.name}</p>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
