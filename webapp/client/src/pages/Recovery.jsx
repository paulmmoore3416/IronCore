import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'
import {
  HeartIcon,
  MoonIcon,
  BoltIcon,
  FireIcon,
  ClockIcon,
  ChartBarIcon,
  SparklesIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
} from '@heroicons/react/24/outline'
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  BarChart,
  Bar,
  RadarChart,
  Radar,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts'

const SLEEP_STAGES = [
  { name: 'Awake', color: '#ef4444', percentage: 5 },
  { name: 'Light Sleep', color: '#3b82f6', percentage: 50 },
  { name: 'Deep Sleep', color: '#8b5cf6', percentage: 25 },
  { name: 'REM Sleep', color: '#10b981', percentage: 20 },
]

const RECOVERY_FACTORS = [
  { factor: 'Sleep Quality', value: 87, optimal: 85 },
  { factor: 'HRV', value: 65, optimal: 60 },
  { factor: 'Resting HR', value: 58, optimal: 60 },
  { factor: 'Muscle Soreness', value: 3, optimal: 2 },
  { factor: 'Stress Level', value: 4, optimal: 3 },
  { factor: 'Energy Level', value: 8, optimal: 8 },
]

export default function Recovery() {
  const { token } = useAuthStore()
  const queryClient = useQueryClient()
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0])

  // Fetch recovery score
  const { data: recoveryData } = useQuery({
    queryKey: ['recovery-score'],
    queryFn: async () => {
      const response = await axios.get('/api/recovery/score', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data
    },
  })

  // Fetch sleep data
  const { data: sleepData } = useQuery({
    queryKey: ['sleep-data', selectedDate],
    queryFn: async () => {
      const response = await axios.get(`/api/recovery/sleep?date=${selectedDate}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data
    },
  })

  // Fetch HRV trends
  const { data: hrvTrends } = useQuery({
    queryKey: ['hrv-trends'],
    queryFn: async () => {
      const response = await axios.get('/api/recovery/hrv-trends', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.trends || []
    },
  })

  // Log recovery metrics
  const logRecovery = useMutation({
    mutationFn: async (data) => {
      await axios.post('/api/recovery/log', data, {
        headers: { Authorization: `Bearer ${token}` },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['recovery-score'])
      toast.success('Recovery metrics logged!')
    },
  })

  const recoveryScore = recoveryData?.score || 0
  const readiness = recoveryScore >= 80 ? 'High' : recoveryScore >= 60 ? 'Moderate' : 'Low'
  const readinessColor = recoveryScore >= 80 ? 'green' : recoveryScore >= 60 ? 'orange' : 'red'

  // Mock sleep timeline data
  const sleepTimeline = [
    { time: '22:00', stage: 'Awake' },
    { time: '22:30', stage: 'Light' },
    { time: '23:00', stage: 'Deep' },
    { time: '23:30', stage: 'Deep' },
    { time: '00:00', stage: 'Light' },
    { time: '00:30', stage: 'REM' },
    { time: '01:00', stage: 'Light' },
    { time: '01:30', stage: 'Deep' },
    { time: '02:00', stage: 'Deep' },
    { time: '02:30', stage: 'Light' },
    { time: '03:00', stage: 'REM' },
    { time: '03:30', stage: 'Light' },
    { time: '04:00', stage: 'Deep' },
    { time: '04:30', stage: 'Light' },
    { time: '05:00', stage: 'REM' },
    { time: '05:30', stage: 'Light' },
    { time: '06:00', stage: 'Awake' },
  ]

  // Mock weekly recovery data
  const weeklyRecovery = [
    { day: 'Mon', score: 85, hrv: 62, rhr: 59 },
    { day: 'Tue', score: 78, hrv: 58, rhr: 61 },
    { day: 'Wed', score: 82, hrv: 64, rhr: 58 },
    { day: 'Thu', score: 88, hrv: 68, rhr: 57 },
    { day: 'Fri', score: 75, hrv: 55, rhr: 62 },
    { day: 'Sat', score: 90, hrv: 70, rhr: 56 },
    { day: 'Sun', score: 86, hrv: 65, rhr: 58 },
  ]

  return (
    <div className="min-h-screen bg-ironcore-darker p-6">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-white">Recovery & Sleep Tracking</h1>
            <p className="text-gray-400 mt-1">Monitor your recovery and optimize your rest</p>
          </div>
          <input
            type="date"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="px-4 py-2 bg-gray-800 text-white rounded-lg border border-gray-700 focus:border-primary-500 focus:outline-none"
          />
        </div>

        {/* Recovery Score Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className={`card bg-gradient-to-br from-${readinessColor}-600 to-${readinessColor}-800`}>
            <div className="flex items-center justify-between">
              <div>
                <p className={`text-${readinessColor}-100 text-sm mb-1`}>Recovery Score</p>
                <p className="text-4xl font-bold text-white">{recoveryScore}</p>
                <p className={`text-${readinessColor}-200 text-xs mt-1`}>{readiness} Readiness</p>
              </div>
              <SparklesIcon className={`w-16 h-16 text-${readinessColor}-300 opacity-50`} />
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-purple-500/20 rounded-lg">
                <HeartIcon className="w-6 h-6 text-purple-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">HRV</p>
                <p className="text-2xl font-bold text-white">{recoveryData?.hrv || 65} ms</p>
                <p className="text-green-400 text-xs flex items-center gap-1">
                  <ArrowTrendingUpIcon className="w-3 h-3" />
                  +5 from yesterday
                </p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-red-500/20 rounded-lg">
                <HeartIcon className="w-6 h-6 text-red-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Resting HR</p>
                <p className="text-2xl font-bold text-white">{recoveryData?.restingHR || 58} bpm</p>
                <p className="text-green-400 text-xs flex items-center gap-1">
                  <ArrowTrendingDownIcon className="w-3 h-3" />
                  -2 from yesterday
                </p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-blue-500/20 rounded-lg">
                <MoonIcon className="w-6 h-6 text-blue-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Sleep Quality</p>
                <p className="text-2xl font-bold text-white">{sleepData?.quality || 87}%</p>
                <p className="text-gray-400 text-xs">7h 45m duration</p>
              </div>
            </div>
          </div>
        </div>

        {/* Readiness Recommendation */}
        <div className={`card border-2 border-${readinessColor}-500`}>
          <div className="flex items-start gap-4">
            <div className={`p-3 bg-${readinessColor}-500/20 rounded-lg`}>
              {readiness === 'High' ? (
                <CheckCircleIcon className={`w-8 h-8 text-${readinessColor}-400`} />
              ) : (
                <ExclamationTriangleIcon className={`w-8 h-8 text-${readinessColor}-400`} />
              )}
            </div>
            <div className="flex-1">
              <h3 className="text-xl font-bold text-white mb-2">Today's Recommendation</h3>
              <p className="text-gray-300 mb-3">
                {readiness === 'High' 
                  ? 'Your body is well-recovered and ready for intense training. This is a great day to push your limits and tackle challenging workouts.'
                  : readiness === 'Moderate'
                  ? 'Your recovery is moderate. Consider a lighter training session or focus on technique work. Listen to your body.'
                  : 'Your body needs more recovery. Consider taking a rest day or doing very light activity like walking or stretching.'}
              </p>
              <div className="flex gap-2">
                <span className={`px-3 py-1 bg-${readinessColor}-500/20 text-${readinessColor}-400 text-sm font-semibold rounded-full`}>
                  {readiness === 'High' ? 'High Intensity OK' : readiness === 'Moderate' ? 'Moderate Intensity' : 'Rest Recommended'}
                </span>
                <span className="px-3 py-1 bg-gray-700 text-gray-300 text-sm rounded-full">
                  {recoveryData?.recommendation || 'Focus on recovery'}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Charts Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Weekly Recovery Trend */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Weekly Recovery Trend</h2>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={weeklyRecovery}>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="day" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
                  labelStyle={{ color: '#fff' }}
                />
                <Legend />
                <Line type="monotone" dataKey="score" stroke="#10b981" strokeWidth={2} name="Recovery Score" />
                <Line type="monotone" dataKey="hrv" stroke="#8b5cf6" strokeWidth={2} name="HRV" />
              </LineChart>
            </ResponsiveContainer>
          </div>

          {/* Sleep Stages Distribution */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Sleep Stages (Last Night)</h2>
            <div className="space-y-4">
              {SLEEP_STAGES.map((stage) => (
                <div key={stage.name}>
                  <div className="flex justify-between text-sm mb-1">
                    <span className="text-gray-300">{stage.name}</span>
                    <span className="text-white font-semibold">{stage.percentage}%</span>
                  </div>
                  <div className="w-full bg-gray-700 rounded-full h-3">
                    <div
                      className="h-3 rounded-full transition-all"
                      style={{ 
                        width: `${stage.percentage}%`,
                        backgroundColor: stage.color 
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
            <div className="mt-6 grid grid-cols-2 gap-4">
              <div className="bg-ironcore-dark rounded-lg p-3">
                <p className="text-gray-400 text-sm">Total Sleep</p>
                <p className="text-white font-bold text-xl">7h 45m</p>
              </div>
              <div className="bg-ironcore-dark rounded-lg p-3">
                <p className="text-gray-400 text-sm">Sleep Efficiency</p>
                <p className="text-green-400 font-bold text-xl">92%</p>
              </div>
            </div>
          </div>

          {/* Recovery Factors Radar */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Recovery Factors</h2>
            <ResponsiveContainer width="100%" height={300}>
              <RadarChart data={RECOVERY_FACTORS.map(f => ({ factor: f.factor, value: f.value }))}>
                <PolarGrid stroke="#374151" />
                <PolarAngleAxis dataKey="factor" stroke="#9ca3af" />
                <PolarRadiusAxis angle={90} domain={[0, 10]} stroke="#9ca3af" />
                <Radar name="Current" dataKey="value" stroke="#10b981" fill="#10b981" fillOpacity={0.6} />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
                />
              </RadarChart>
            </ResponsiveContainer>
          </div>

          {/* Sleep Timeline */}
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Sleep Timeline</h2>
            <div className="space-y-2">
              {sleepTimeline.map((entry, idx) => (
                <div key={idx} className="flex items-center gap-3">
                  <span className="text-gray-400 text-sm w-16">{entry.time}</span>
                  <div className="flex-1 h-8 rounded flex items-center px-3" style={{
                    backgroundColor: 
                      entry.stage === 'Awake' ? '#ef444420' :
                      entry.stage === 'Light' ? '#3b82f620' :
                      entry.stage === 'Deep' ? '#8b5cf620' :
                      '#10b98120'
                  }}>
                    <span className="text-white text-sm font-medium">{entry.stage}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Recovery Tips */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="card">
            <div className="flex items-center gap-3 mb-3">
              <div className="p-2 bg-blue-500/20 rounded-lg">
                <MoonIcon className="w-6 h-6 text-blue-400" />
              </div>
              <h3 className="text-white font-semibold">Sleep Optimization</h3>
            </div>
            <ul className="space-y-2 text-sm text-gray-300">
              <li className="flex items-start gap-2">
                <CheckCircleIcon className="w-4 h-4 text-green-400 flex-shrink-0 mt-0.5" />
                <span>Maintain consistent sleep schedule</span>
              </li>
              <li className="flex items-start gap-2">
                <CheckCircleIcon className="w-4 h-4 text-green-400 flex-shrink-0 mt-0.5" />
                <span>Keep bedroom cool (65-68°F)</span>
              </li>
              <li className="flex items-start gap-2">
                <CheckCircleIcon className="w-4 h-4 text-green-400 flex-shrink-0 mt-0.5" />
                <span>Avoid screens 1 hour before bed</span>
              </li>
            </ul>
          </div>

          <div className="card">
            <div className="flex items-center gap-3 mb-3">
              <div className="p-2 bg-purple-500/20 rounded-lg">
                <HeartIcon className="w-6 h-6 text-purple-400" />
              </div>
              <h3 className="text-white font-semibold">HRV Improvement</h3>
            </div>
            <ul className="space-y-2 text-sm text-gray-300">
              <li className="flex items-start gap-2">
                <CheckCircleIcon className="w-4 h-4 text-green-400 flex-shrink-0 mt-0.5" />
                <span>Practice deep breathing exercises</span>
              </li>
              <li className="flex items-start gap-2">
                <CheckCircleIcon className="w-4 h-4 text-green-400 flex-shrink-0 mt-0.5" />
                <span>Manage stress through meditation</span>
              </li>
              <li className="flex items-start gap-2">
                <CheckCircleIcon className="w-4 h-4 text-green-400 flex-shrink-0 mt-0.5" />
                <span>Stay hydrated throughout day</span>
              </li>
            </ul>
          </div>

          <div className="card">
            <div className="flex items-center gap-3 mb-3">
              <div className="p-2 bg-green-500/20 rounded-lg">
                <BoltIcon className="w-6 h-6 text-green-400" />
              </div>
              <h3 className="text-white font-semibold">Active Recovery</h3>
            </div>
            <ul className="space-y-2 text-sm text-gray-300">
              <li className="flex items-start gap-2">
                <CheckCircleIcon className="w-4 h-4 text-green-400 flex-shrink-0 mt-0.5" />
                <span>Light walking or swimming</span>
              </li>
              <li className="flex items-start gap-2">
                <CheckCircleIcon className="w-4 h-4 text-green-400 flex-shrink-0 mt-0.5" />
                <span>Foam rolling and stretching</span>
              </li>
              <li className="flex items-start gap-2">
                <CheckCircleIcon className="w-4 h-4 text-green-400 flex-shrink-0 mt-0.5" />
                <span>Yoga or mobility work</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}
