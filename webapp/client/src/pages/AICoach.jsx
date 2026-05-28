import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'
import {
  SparklesIcon,
  LightBulbIcon,
  ChartBarIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon,
  FireIcon,
  HeartIcon,
  BoltIcon,
  TrophyIcon,
  ClockIcon,
  ArrowTrendingUpIcon,
} from '@heroicons/react/24/outline'

const RECOMMENDATION_CATEGORIES = [
  { id: 'workout', name: 'Workout Suggestions', icon: FireIcon, color: 'orange' },
  { id: 'nutrition', name: 'Nutrition Tips', icon: HeartIcon, color: 'red' },
  { id: 'recovery', name: 'Recovery Advice', icon: SparklesIcon, color: 'purple' },
  { id: 'form', name: 'Form Analysis', icon: CheckCircleIcon, color: 'green' },
  { id: 'injury', name: 'Injury Prevention', icon: ExclamationTriangleIcon, color: 'yellow' },
  { id: 'performance', name: 'Performance Tips', icon: BoltIcon, color: 'blue' },
]

export default function AICoach() {
  const { token } = useAuthStore()
  const queryClient = useQueryClient()
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [chatMessage, setChatMessage] = useState('')

  // Fetch AI recommendations
  const { data: recommendations, isLoading } = useQuery({
    queryKey: ['ai-recommendations', selectedCategory],
    queryFn: async () => {
      const response = await axios.get(`/api/ai/recommendations?category=${selectedCategory}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.recommendations || []
    },
  })

  // Fetch AI insights
  const { data: insights } = useQuery({
    queryKey: ['ai-insights'],
    queryFn: async () => {
      const response = await axios.get('/api/ai/insights', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.insights || {}
    },
  })

  // Fetch form analysis
  const { data: formAnalysis } = useQuery({
    queryKey: ['form-analysis'],
    queryFn: async () => {
      const response = await axios.get('/api/ai/form-analysis', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.analysis || []
    },
  })

  // Chat with AI
  const chatWithAI = useMutation({
    mutationFn: async (message) => {
      const response = await axios.post('/api/ai/chat', 
        { message },
        { headers: { Authorization: `Bearer ${token}` } }
      )
      return response.data
    },
    onSuccess: (data) => {
      toast.success('AI response received!')
      setChatMessage('')
    },
  })

  const handleChat = (e) => {
    e.preventDefault()
    if (chatMessage.trim()) {
      chatWithAI.mutate(chatMessage)
    }
  }

  return (
    <div className="min-h-screen bg-ironcore-darker p-6">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-white flex items-center gap-3">
              <SparklesIcon className="w-8 h-8 text-primary-500" />
              AI Coach & Recommendations
            </h1>
            <p className="text-gray-400 mt-1">Personalized insights powered by artificial intelligence</p>
          </div>
        </div>

        {/* AI Insights Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="card bg-gradient-to-br from-blue-600 to-blue-800">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-100 text-sm mb-1">AI Confidence</p>
                <p className="text-4xl font-bold text-white">{insights?.confidence || 92}%</p>
                <p className="text-blue-200 text-xs mt-1">High Accuracy</p>
              </div>
              <SparklesIcon className="w-16 h-16 text-blue-300 opacity-50" />
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-green-500/20 rounded-lg">
                <CheckCircleIcon className="w-6 h-6 text-green-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Active Tips</p>
                <p className="text-2xl font-bold text-white">{recommendations?.length || 0}</p>
                <p className="text-gray-400 text-xs">Personalized</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-orange-500/20 rounded-lg">
                <LightBulbIcon className="w-6 h-6 text-orange-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Insights</p>
                <p className="text-2xl font-bold text-white">{insights?.totalInsights || 24}</p>
                <p className="text-green-400 text-xs">+3 this week</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-purple-500/20 rounded-lg">
                <TrophyIcon className="w-6 h-6 text-purple-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Improvements</p>
                <p className="text-2xl font-bold text-white">{insights?.improvements || 15}</p>
                <p className="text-gray-400 text-xs">Suggested</p>
              </div>
            </div>
          </div>
        </div>

        {/* Category Filter */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Recommendation Categories</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
            <button
              onClick={() => setSelectedCategory('all')}
              className={`p-4 rounded-xl border-2 transition-all hover:scale-105 ${
                selectedCategory === 'all'
                  ? 'border-primary-500 bg-primary-500/10'
                  : 'border-gray-700 bg-ironcore-dark hover:border-gray-600'
              }`}
            >
              <div className="text-3xl mb-2">🎯</div>
              <p className="text-white font-semibold text-sm">All</p>
            </button>
            {RECOMMENDATION_CATEGORIES.map((category) => (
              <button
                key={category.id}
                onClick={() => setSelectedCategory(category.id)}
                className={`p-4 rounded-xl border-2 transition-all hover:scale-105 ${
                  selectedCategory === category.id
                    ? 'border-primary-500 bg-primary-500/10'
                    : 'border-gray-700 bg-ironcore-dark hover:border-gray-600'
                }`}
              >
                <category.icon className={`w-8 h-8 mx-auto mb-2 text-${category.color}-400`} />
                <p className="text-white font-semibold text-sm">{category.name}</p>
              </button>
            ))}
          </div>
        </div>

        {/* AI Recommendations */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Personalized Recommendations</h2>
          {isLoading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div>
            </div>
          ) : recommendations && recommendations.length > 0 ? (
            <div className="space-y-4">
              {recommendations.map((rec, idx) => (
                <div key={idx} className="bg-ironcore-dark rounded-lg p-4 border-l-4 border-primary-500">
                  <div className="flex items-start gap-4">
                    <div className={`p-3 bg-${rec.color || 'blue'}-500/20 rounded-lg flex-shrink-0`}>
                      <SparklesIcon className={`w-6 h-6 text-${rec.color || 'blue'}-400`} />
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center justify-between mb-2">
                        <h3 className="text-white font-semibold text-lg">{rec.title}</h3>
                        <span className={`px-3 py-1 bg-${rec.priority === 'high' ? 'red' : rec.priority === 'medium' ? 'orange' : 'green'}-500/20 text-${rec.priority === 'high' ? 'red' : rec.priority === 'medium' ? 'orange' : 'green'}-400 text-xs font-semibold rounded-full`}>
                          {rec.priority || 'medium'} priority
                        </span>
                      </div>
                      <p className="text-gray-300 mb-3">{rec.description}</p>
                      <div className="flex items-center gap-4 text-sm">
                        <div className="flex items-center gap-2 text-gray-400">
                          <ChartBarIcon className="w-4 h-4" />
                          <span>Impact: {rec.impact || 'High'}</span>
                        </div>
                        <div className="flex items-center gap-2 text-gray-400">
                          <ClockIcon className="w-4 h-4" />
                          <span>Effort: {rec.effort || 'Medium'}</span>
                        </div>
                        <div className="flex items-center gap-2 text-green-400">
                          <ArrowTrendingUpIcon className="w-4 h-4" />
                          <span>+{rec.expectedImprovement || '15'}% improvement</span>
                        </div>
                      </div>
                      {rec.actionable && (
                        <button className="mt-3 px-4 py-2 bg-primary-500 hover:bg-primary-600 text-white text-sm font-semibold rounded-lg transition-colors">
                          Apply Recommendation
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <SparklesIcon className="w-16 h-16 text-gray-600 mx-auto mb-4" />
              <p className="text-gray-400">No recommendations available. Keep training to get personalized insights!</p>
            </div>
          )}
        </div>

        {/* Form Analysis */}
        {formAnalysis && formAnalysis.length > 0 && (
          <div className="card">
            <h2 className="text-xl font-bold text-white mb-4">Form Analysis & Corrections</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {formAnalysis.map((analysis, idx) => (
                <div key={idx} className="bg-ironcore-dark rounded-lg p-4">
                  <div className="flex items-center gap-3 mb-3">
                    <div className={`p-2 bg-${analysis.status === 'good' ? 'green' : analysis.status === 'warning' ? 'yellow' : 'red'}-500/20 rounded-lg`}>
                      {analysis.status === 'good' ? (
                        <CheckCircleIcon className="w-6 h-6 text-green-400" />
                      ) : (
                        <ExclamationTriangleIcon className="w-6 h-6 text-yellow-400" />
                      )}
                    </div>
                    <div>
                      <h3 className="text-white font-semibold">{analysis.exercise}</h3>
                      <p className="text-gray-400 text-sm">{analysis.date}</p>
                    </div>
                  </div>
                  <p className="text-gray-300 text-sm mb-3">{analysis.feedback}</p>
                  {analysis.corrections && (
                    <div className="space-y-2">
                      <p className="text-white font-semibold text-sm">Corrections:</p>
                      <ul className="space-y-1">
                        {analysis.corrections.map((correction, i) => (
                          <li key={i} className="text-gray-300 text-sm flex items-start gap-2">
                            <span className="text-primary-500">•</span>
                            <span>{correction}</span>
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* AI Chat Interface */}
        <div className="card">
          <h2 className="text-xl font-bold text-white mb-4">Ask Your AI Coach</h2>
          <form onSubmit={handleChat} className="space-y-4">
            <div className="flex gap-3">
              <input
                type="text"
                value={chatMessage}
                onChange={(e) => setChatMessage(e.target.value)}
                placeholder="Ask anything about training, nutrition, or recovery..."
                className="flex-1 px-4 py-3 bg-ironcore-dark text-white rounded-lg border border-gray-700 focus:border-primary-500 focus:outline-none"
              />
              <button
                type="submit"
                disabled={!chatMessage.trim() || chatWithAI.isPending}
                className="px-6 py-3 bg-primary-500 hover:bg-primary-600 disabled:bg-gray-700 disabled:cursor-not-allowed text-white font-semibold rounded-lg transition-colors flex items-center gap-2"
              >
                <SparklesIcon className="w-5 h-5" />
                Ask AI
              </button>
            </div>
          </form>

          {chatWithAI.data && (
            <div className="mt-4 bg-ironcore-dark rounded-lg p-4">
              <div className="flex items-start gap-3">
                <div className="p-2 bg-primary-500/20 rounded-lg">
                  <SparklesIcon className="w-5 h-5 text-primary-400" />
                </div>
                <div className="flex-1">
                  <p className="text-white font-semibold mb-2">AI Coach Response:</p>
                  <p className="text-gray-300">{chatWithAI.data.response}</p>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Quick Tips */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="card">
            <div className="flex items-center gap-3 mb-3">
              <div className="p-2 bg-blue-500/20 rounded-lg">
                <LightBulbIcon className="w-6 h-6 text-blue-400" />
              </div>
              <h3 className="text-white font-semibold">Training Tip</h3>
            </div>
            <p className="text-gray-300 text-sm">
              Focus on progressive overload by increasing weight by 2.5-5% when you can complete all sets with good form.
            </p>
          </div>

          <div className="card">
            <div className="flex items-center gap-3 mb-3">
              <div className="p-2 bg-green-500/20 rounded-lg">
                <HeartIcon className="w-6 h-6 text-green-400" />
              </div>
              <h3 className="text-white font-semibold">Nutrition Tip</h3>
            </div>
            <p className="text-gray-300 text-sm">
              Consume 0.8-1g of protein per pound of body weight daily to support muscle recovery and growth.
            </p>
          </div>

          <div className="card">
            <div className="flex items-center gap-3 mb-3">
              <div className="p-2 bg-purple-500/20 rounded-lg">
                <SparklesIcon className="w-6 h-6 text-purple-400" />
              </div>
              <h3 className="text-white font-semibold">Recovery Tip</h3>
            </div>
            <p className="text-gray-300 text-sm">
              Aim for 7-9 hours of quality sleep per night to optimize recovery and performance.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
