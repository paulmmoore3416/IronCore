import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import GamificationPanel from '../components/GamificationPanel'
import {
  HeartIcon,
  FireIcon,
  ScaleIcon,
  ClockIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
} from '@heroicons/react/24/outline'
import { LineChart, Line, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'

export default function Dashboard() {
  const { token } = useAuthStore()

  const { data: dashboardData, isLoading } = useQuery({
    queryKey: ['dashboard'],
    queryFn: async () => {
      const response = await axios.get('/api/metrics/dashboard', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.dashboard
    },
  })

  const { data: trendsData } = useQuery({
    queryKey: ['trends', 'steps'],
    queryFn: async () => {
      const response = await axios.get('/api/metrics/trends/steps?period=7d', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data
    },
  })

  const metrics = [
    {
      name: 'Steps',
      value: dashboardData?.steps || 0,
      unit: 'steps',
      icon: FireIcon,
      color: 'text-orange-400',
      bgColor: 'bg-orange-900/20',
    },
    {
      name: 'Heart Rate',
      value: dashboardData?.heartRate || 0,
      unit: 'bpm',
      icon: HeartIcon,
      color: 'text-red-400',
      bgColor: 'bg-red-900/20',
    },
    {
      name: 'Weight',
      value: dashboardData?.weight || 0,
      unit: 'kg',
      icon: ScaleIcon,
      color: 'text-blue-400',
      bgColor: 'bg-blue-900/20',
    },
    {
      name: 'Active Calories',
      value: dashboardData?.activeCalories || 0,
      unit: 'kcal',
      icon: FireIcon,
      color: 'text-yellow-400',
      bgColor: 'bg-yellow-900/20',
    },
  ]

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="skeleton h-32 rounded-lg"></div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="skeleton h-40 rounded-lg"></div>
          ))}
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-white">Dashboard</h1>
        <p className="text-gray-400 mt-1">Your fitness overview</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {metrics.map((metric) => {
          const Icon = metric.icon
          return (
            <div key={metric.name} className="metric-card">
              <div className="flex items-center justify-between mb-4">
                <div className={`p-3 rounded-lg ${metric.bgColor}`}>
                  <Icon className={`h-6 w-6 ${metric.color}`} />
                </div>
              </div>
              <div>
                <p className="text-gray-400 text-sm">{metric.name}</p>
                <p className="text-3xl font-bold text-white mt-1">
                  {metric.value.toLocaleString()}
                  <span className="text-lg text-gray-400 ml-2">{metric.unit}</span>
                </p>
              </div>
            </div>
          )
        })}
      </div>

      {trendsData && trendsData.data && trendsData.data.length > 0 && (
        <div className="card">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-white">Steps Trend (7 Days)</h2>
            <div className="flex items-center gap-2">
              {trendsData.statistics?.trend === 'up' ? (
                <ArrowTrendingUpIcon className="h-5 w-5 text-green-400" />
              ) : trendsData.statistics?.trend === 'down' ? (
                <ArrowTrendingDownIcon className="h-5 w-5 text-red-400" />
              ) : null}
              <span className={`text-sm font-medium ${
                trendsData.statistics?.trend === 'up' ? 'text-green-400' : 
                trendsData.statistics?.trend === 'down' ? 'text-red-400' : 
                'text-gray-400'
              }`}>
                {trendsData.statistics?.trendPercentage > 0 ? '+' : ''}
                {trendsData.statistics?.trendPercentage || 0}%
              </span>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={trendsData.data}>
              <defs>
                <linearGradient id="colorSteps" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#0ea5e9" stopOpacity={0.8}/>
                  <stop offset="95%" stopColor="#0ea5e9" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis 
                dataKey="timestamp" 
                stroke="#9ca3af"
                tickFormatter={(value) => new Date(value).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
              />
              <YAxis stroke="#9ca3af" />
              <Tooltip 
                contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
                labelStyle={{ color: '#9ca3af' }}
              />
              <Area 
                type="monotone" 
                dataKey="value" 
                stroke="#0ea5e9" 
                fillOpacity={1} 
                fill="url(#colorSteps)" 
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-xl font-semibold text-white mb-4">Quick Stats</h2>
          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <span className="text-gray-400">Average Steps</span>
              <span className="text-white font-semibold">
                {trendsData?.statistics?.avg?.toLocaleString() || 0}
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-400">Peak Steps</span>
              <span className="text-white font-semibold">
                {trendsData?.statistics?.max?.toLocaleString() || 0}
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-400">Total Workouts</span>
              <span className="text-white font-semibold">12</span>
            </div>
          </div>
        </div>

        <div className="card">
          <h2 className="text-xl font-semibold text-white mb-4">Today's Goals</h2>
          <div className="space-y-4">
            <div>
              <div className="flex justify-between text-sm mb-1">
                <span className="text-gray-400">Steps</span>
                <span className="text-white">{dashboardData?.steps || 0} / 10,000</span>
              </div>
              <div className="w-full bg-gray-700 rounded-full h-2">
                <div 
                  className="bg-primary-500 h-2 rounded-full transition-all duration-300"
                  style={{ width: `${Math.min((dashboardData?.steps || 0) / 10000 * 100, 100)}%` }}
                ></div>
              </div>
            </div>
            <div>
              <div className="flex justify-between text-sm mb-1">
                <span className="text-gray-400">Calories</span>
                <span className="text-white">{dashboardData?.activeCalories || 0} / 500</span>
              </div>
              <div className="w-full bg-gray-700 rounded-full h-2">
                <div 
                  className="bg-orange-500 h-2 rounded-full transition-all duration-300"
                  style={{ width: `${Math.min((dashboardData?.activeCalories || 0) / 500 * 100, 100)}%` }}
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Gamification Section */}
      <div>
        <h2 className="text-2xl font-bold text-white mb-4">Your Progress & Achievements</h2>
        <GamificationPanel />
      </div>
    </div>
  )
}
