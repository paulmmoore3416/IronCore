import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import {
  HeartIcon,
  FireIcon,
  BoltIcon,
  ScaleIcon,
  ClockIcon,
  TrophyIcon,
  ChartBarIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  CalendarIcon,
} from '@heroicons/react/24/outline'
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  Radar,
} from 'recharts'

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899']

const TIME_PERIODS = [
  { id: '7d', name: '7 Days' },
  { id: '30d', name: '30 Days' },
  { id: '90d', name: '90 Days' },
  { id: '1y', name: '1 Year' },
]

const METRIC_TYPES = [
  { id: 'steps', name: 'Steps', icon: '👣', color: 'blue' },
  { id: 'heart-rate', name: 'Heart Rate', icon: '❤️', color: 'red' },
  { id: 'calories', name: 'Calories', icon: '🔥', color: 'orange' },
  { id: 'sleep', name: 'Sleep', icon: '😴', color: 'purple' },
  { id: 'weight', name: 'Weight', icon: '⚖️', color: 'green' },
  { id: 'water', name: 'Hydration', icon: '💧', color: 'cyan' },
]

export default function Metrics() {
  const { token } = useAuthStore()
  const [selectedPeriod, setSelectedPeriod] = useState('30d')
  const [selectedMetric, setSelectedMetric] = useState('steps')

  // Fetch dashboard metrics
  const { data: dashboardData, isLoading: dashboardLoading } = useQuery({
    queryKey: ['metrics-dashboard'],
    queryFn: async () => {
      const response = await axios.get('/api/metrics/dashboard', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.dashboard || {}
    },
  })

  // Fetch trends data
  const { data: trendsData, isLoading: trendsLoading } = useQuery({
    queryKey: ['metrics-trends', selectedMetric, selectedPeriod],
    queryFn: async () => {
      const response = await axios.get(`/api/metrics/trends/${selectedMetric}?period=${selectedPeriod}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data
    },
  })

  // Fetch health score
  const { data: healthScore } = useQuery({
    queryKey: ['health-score'],
    queryFn: async () => {
      const response = await axios.get('/api/metrics/health-score', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.score || {}
    },
  })

  // Fetch body composition
  const { data: bodyComp } = useQuery({
    queryKey: ['body-composition'],
    queryFn: async () => {
      const response = await axios.get('/api/metrics/body-composition', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.composition || {}
    },
  })

  // Mock data for charts (replace with real data)
  const weeklyActivityData = [
    { day: 'Mon', steps: 8500, calories: 2200, active: 45 },
    { day: 'Tue', steps: 12000, calories: 2800, active: 65 },
    { day: 'Wed', steps: 6500, calories: 1900, active: 30 },
    { day: 'Thu', steps: 15000, calories: 3200, active: 85 },
    { day: 'Fri', steps: 10500, calories: 2500, active: 55 },
    { day: 'Sat', steps: 18000, calories: 3500, active: 95 },
    { day: 'Sun', steps: 9000, calories: 2300, active: 50 },
  ]

  const heartRateZones = [
    { name: 'Rest', value: 25, color: '#10b981' },
    { name: 'Fat Burn', value: 35, color: '#3b82f6' },
    { name: 'Cardio', value: 25, color: '#f59e0b' },
    { name: 'Peak', value: 15, color: '#ef4444' },
  ]

  const bodyMetrics = [
    { metric: 'Strength', value: 85 },
    { metric: 'Endurance', value: 72 },
    { metric: 'Flexibility', value: 65 },
    { metric: 'Balance', value: 78 },
    { metric: 'Speed', value: 70 },
    { metric: 'Recovery', value: 80 },
  ]

  const monthlyProgress = [
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
            <h1 className="text-3xl font-bold text-white">Advanced Metrics & Analytics</h1>
            <p className="text-gray-400 mt-1">Comprehensive health and performance tracking</p>
          </div>
          <div className="flex gap-2">
            {TIME_PERIODS.map((period) => (
              <button
                key={period.id}
                onClick={() => setSelectedPeriod(period.id)}
                className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                  selectedPeriod === period.id
                    ? 'bg-primary-500 text-white'
                    : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
                }`}
              >
                {period.name}
              </button>
            ))}
          </div>
        </div>

        {/* Health Score Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="card bg-gradient-to-br from-blue-600 to-blue-800">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-100 text-sm mb-1">Overall Health Score</p>
                <p className="text-4xl font-bold text-white">{healthScore?.overall || 85}</p>
                <p className="text-blue-200 text-xs mt-1">Excellent</p>
              </div>
              <TrophyIcon className="w-16 h-16 text-blue-300 opacity-50" />
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-red-500/20 rounded-lg">
                <HeartIcon className="w-6 h-6 text-red-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Avg Heart Rate</p>
                <p className="text-2xl font-bold text-white">{dashboardData?.heartRate || 72} bpm</p>
                <p className="text-green-400 text-xs flex items-center gap-1">
                  <ArrowTrendingDownIcon className="w-3 h-3" />
                  -3 from last week
                </p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-orange-500/20 rounded-lg">
                <FireIcon className="w-6 h-6 text-orange-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Daily Calories</p>
                <p className="text-2xl font-bold text-white">{dashboardData?.calories || 2450}</p>
                <p className="text-green-400 text-xs flex items-center gap-1">
                  <ArrowTrendingUpIcon className="w-3 h-3" />
                  +150 from target
                </p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-purple-500/20 rounded-lg">
                <ClockIcon className="w-6 h-6 text-purple-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Sleep Quality</p>
                <p className="text-2xl font-bold text-white">{dashboardData?.sleepScore || 87}%</p>
                <p className="text-gray-400 text-xs">7h 45m avg</p>
              </div>
            </div>
          </div>
        </div>

        {/* Metric Type Selector */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Track Your Metrics</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
            {METRIC_TYPES.map((metric) => (
              <button
                key={metric.id}
                onClick={() => setSelectedMetric(metric.id)}
                className={`p-4 rounded-xl border-2 transition-all hover:scale-105 ${
                  selectedMetric === metric.id
                    ? 'border-primary-500 bg-primary-500/10'
                    : 'border-gray-700 bg-ironcore-dark hover:border-gray-600'
                }`}
              >
                <div className="text-3xl mb-2">{metric.icon}</div>
                <p className="text-white font-semibold text-sm">{metric.name}</p>
              </button>
            ))}
          </div>
        </div>

        {/* Main Charts Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Weekly Activity Chart */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Weekly Activity Overview</h2>
            <ResponsiveContainer width="100%" height={300}>
              <AreaChart data={weeklyActivityData}>
                <defs>
                  <linearGradient id="colorSteps" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                  </linearGradient>
                  <linearGradient id="colorCalories" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#f59e0b" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#f59e0b" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="day" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
                  labelStyle={{ color: '#fff' }}
                />
                <Legend />
                <Area type="monotone" dataKey="steps" stroke="#3b82f6" fillOpacity={1} fill="url(#colorSteps)" />
                <Area type="monotone" dataKey="calories" stroke="#f59e0b" fillOpacity={1} fill="url(#colorCalories)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>

          {/* Heart Rate Zones */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Heart Rate Zones Distribution</h2>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={heartRateZones}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={100}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {heartRateZones.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Body Metrics Radar */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Performance Profile</h2>
            <ResponsiveContainer width="100%" height={300}>
              <RadarChart data={bodyMetrics}>
                <PolarGrid stroke="#374151" />
                <PolarAngleAxis dataKey="metric" stroke="#9ca3af" />
                <PolarRadiusAxis angle={90} domain={[0, 100]} stroke="#9ca3af" />
                <Radar name="Performance" dataKey="value" stroke="#3b82f6" fill="#3b82f6" fillOpacity={0.6} />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
                />
              </RadarChart>
            </ResponsiveContainer>
          </div>

          {/* Monthly Progress */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Body Composition Trends</h2>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={monthlyProgress}>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="month" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
                  labelStyle={{ color: '#fff' }}
                />
                <Legend />
                <Line type="monotone" dataKey="weight" stroke="#10b981" strokeWidth={2} />
                <Line type="monotone" dataKey="bodyFat" stroke="#ef4444" strokeWidth={2} />
                <Line type="monotone" dataKey="muscle" stroke="#3b82f6" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Detailed Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="card">
            <h3 className="text-lg font-semibold text-white mb-4">Daily Averages</h3>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-gray-400">Steps</span>
                <span className="text-white font-semibold">11,250</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-400">Active Minutes</span>
                <span className="text-white font-semibold">62 min</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-400">Calories Burned</span>
                <span className="text-white font-semibold">2,650</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-400">Distance</span>
                <span className="text-white font-semibold">8.5 km</span>
              </div>
            </div>
          </div>

          <div className="card">
            <h3 className="text-lg font-semibold text-white mb-4">Personal Records</h3>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-gray-400">Max Steps (Day)</span>
                <span className="text-green-400 font-semibold">22,450</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-400">Longest Run</span>
                <span className="text-green-400 font-semibold">15.2 km</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-400">Max Heart Rate</span>
                <span className="text-green-400 font-semibold">185 bpm</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-400">Best Sleep Score</span>
                <span className="text-green-400 font-semibold">98%</span>
              </div>
            </div>
          </div>

          <div className="card">
            <h3 className="text-lg font-semibold text-white mb-4">Achievements</h3>
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-yellow-500/20 rounded-full flex items-center justify-center">
                  <TrophyIcon className="w-6 h-6 text-yellow-400" />
                </div>
                <div>
                  <p className="text-white font-semibold text-sm">7-Day Streak</p>
                  <p className="text-gray-400 text-xs">Workout every day</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-blue-500/20 rounded-full flex items-center justify-center">
                  <BoltIcon className="w-6 h-6 text-blue-400" />
                </div>
                <div>
                  <p className="text-white font-semibold text-sm">100K Steps</p>
                  <p className="text-gray-400 text-xs">This week</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-green-500/20 rounded-full flex items-center justify-center">
                  <FireIcon className="w-6 h-6 text-green-400" />
                </div>
                <div>
                  <p className="text-white font-semibold text-sm">Calorie Master</p>
                  <p className="text-gray-400 text-xs">Hit target 30 days</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}