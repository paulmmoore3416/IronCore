import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import {
  TrophyIcon,
  FireIcon,
  StarIcon,
  BoltIcon,
  ShieldCheckIcon,
  SparklesIcon,
  ChartBarIcon,
  CheckCircleIcon,
} from '@heroicons/react/24/outline'

const BADGE_CATEGORIES = {
  strength: { icon: '💪', color: 'red', name: 'Strength' },
  endurance: { icon: '🏃', color: 'blue', name: 'Endurance' },
  consistency: { icon: '🔥', color: 'orange', name: 'Consistency' },
  milestone: { icon: '🎯', color: 'green', name: 'Milestone' },
  social: { icon: '👥', color: 'purple', name: 'Social' },
  achievement: { icon: '🏆', color: 'yellow', name: 'Achievement' },
}

export default function GamificationPanel() {
  const { token } = useAuthStore()
  const [selectedCategory, setSelectedCategory] = useState('all')

  // Fetch gamification stats
  const { data: stats } = useQuery({
    queryKey: ['gamification-stats'],
    queryFn: async () => {
      const response = await axios.get('/api/gamification/stats', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.stats || {}
    },
  })

  // Fetch badges
  const { data: badges } = useQuery({
    queryKey: ['badges', selectedCategory],
    queryFn: async () => {
      const response = await axios.get(`/api/gamification/badges?category=${selectedCategory}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.badges || []
    },
  })

  // Fetch current streak
  const { data: streak } = useQuery({
    queryKey: ['streak'],
    queryFn: async () => {
      const response = await axios.get('/api/gamification/streak', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.streak || {}
    },
  })

  // Fetch level info
  const { data: levelInfo } = useQuery({
    queryKey: ['level-info'],
    queryFn: async () => {
      const response = await axios.get('/api/gamification/level', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.level || {}
    },
  })

  const earnedBadges = badges?.filter(b => b.earned) || []
  const lockedBadges = badges?.filter(b => !b.earned) || []

  return (
    <div className="space-y-6">
      {/* Level & XP Card */}
      <div className="card bg-gradient-to-br from-purple-600 to-blue-600">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className="w-20 h-20 bg-white/20 rounded-full flex items-center justify-center">
              <span className="text-4xl font-bold text-white">{levelInfo?.level || 1}</span>
            </div>
            <div>
              <h3 className="text-white font-bold text-xl">{levelInfo?.title || 'Beginner'}</h3>
              <p className="text-blue-100 text-sm">Level {levelInfo?.level || 1}</p>
              <div className="flex items-center gap-2 mt-2">
                <div className="w-48 bg-white/20 rounded-full h-2">
                  <div
                    className="bg-white h-2 rounded-full transition-all"
                    style={{ width: `${levelInfo?.progressPercent || 0}%` }}
                  />
                </div>
                <span className="text-white text-sm font-semibold">
                  {levelInfo?.currentXP || 0} / {levelInfo?.nextLevelXP || 100} XP
                </span>
              </div>
            </div>
          </div>
          <div className="text-right">
            <p className="text-blue-100 text-sm">Total XP</p>
            <p className="text-4xl font-bold text-white">{levelInfo?.totalXP || 0}</p>
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-orange-500/20 rounded-lg">
              <FireIcon className="w-6 h-6 text-orange-400" />
            </div>
            <div>
              <p className="text-gray-400 text-sm">Current Streak</p>
              <p className="text-2xl font-bold text-white">{streak?.current || 0}</p>
              <p className="text-gray-400 text-xs">days</p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-yellow-500/20 rounded-lg">
              <TrophyIcon className="w-6 h-6 text-yellow-400" />
            </div>
            <div>
              <p className="text-gray-400 text-sm">Badges Earned</p>
              <p className="text-2xl font-bold text-white">{earnedBadges.length}</p>
              <p className="text-gray-400 text-xs">of {badges?.length || 0}</p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-blue-500/20 rounded-lg">
              <BoltIcon className="w-6 h-6 text-blue-400" />
            </div>
            <div>
              <p className="text-gray-400 text-sm">Best Streak</p>
              <p className="text-2xl font-bold text-white">{streak?.longest || 0}</p>
              <p className="text-gray-400 text-xs">days</p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-green-500/20 rounded-lg">
              <StarIcon className="w-6 h-6 text-green-400" />
            </div>
            <div>
              <p className="text-gray-400 text-sm">Rank</p>
              <p className="text-2xl font-bold text-white">#{stats?.rank || 0}</p>
              <p className="text-gray-400 text-xs">Global</p>
            </div>
          </div>
        </div>
      </div>

      {/* Badge Categories */}
      <div className="card">
        <h3 className="text-xl font-bold text-white mb-4">Badge Collection</h3>
        <div className="flex gap-2 mb-4 overflow-x-auto pb-2">
          <button
            onClick={() => setSelectedCategory('all')}
            className={`px-4 py-2 rounded-lg font-semibold text-sm whitespace-nowrap transition-colors ${
              selectedCategory === 'all'
                ? 'bg-primary-500 text-white'
                : 'bg-ironcore-dark text-gray-400 hover:bg-gray-700'
            }`}
          >
            All Badges
          </button>
          {Object.entries(BADGE_CATEGORIES).map(([key, cat]) => (
            <button
              key={key}
              onClick={() => setSelectedCategory(key)}
              className={`px-4 py-2 rounded-lg font-semibold text-sm whitespace-nowrap transition-colors ${
                selectedCategory === key
                  ? 'bg-primary-500 text-white'
                  : 'bg-ironcore-dark text-gray-400 hover:bg-gray-700'
              }`}
            >
              {cat.icon} {cat.name}
            </button>
          ))}
        </div>

        {/* Earned Badges */}
        {earnedBadges.length > 0 && (
          <div className="mb-6">
            <h4 className="text-white font-semibold mb-3 flex items-center gap-2">
              <CheckCircleIcon className="w-5 h-5 text-green-400" />
              Earned ({earnedBadges.length})
            </h4>
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
              {earnedBadges.map((badge) => (
                <div
                  key={badge.id}
                  className="bg-ironcore-dark rounded-lg p-4 border-2 border-green-500/50 hover:border-green-500 transition-all hover:scale-105 cursor-pointer"
                >
                  <div className="text-4xl mb-2 text-center">{badge.icon}</div>
                  <h5 className="text-white font-semibold text-sm text-center mb-1">{badge.name}</h5>
                  <p className="text-gray-400 text-xs text-center">{badge.description}</p>
                  <p className="text-green-400 text-xs text-center mt-2">
                    Earned {badge.earnedDate}
                  </p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Locked Badges */}
        {lockedBadges.length > 0 && (
          <div>
            <h4 className="text-white font-semibold mb-3 flex items-center gap-2">
              <ShieldCheckIcon className="w-5 h-5 text-gray-400" />
              Locked ({lockedBadges.length})
            </h4>
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
              {lockedBadges.map((badge) => (
                <div
                  key={badge.id}
                  className="bg-ironcore-dark rounded-lg p-4 border-2 border-gray-700 opacity-60 hover:opacity-80 transition-all"
                >
                  <div className="text-4xl mb-2 text-center grayscale">{badge.icon}</div>
                  <h5 className="text-gray-400 font-semibold text-sm text-center mb-1">{badge.name}</h5>
                  <p className="text-gray-500 text-xs text-center">{badge.description}</p>
                  <div className="mt-2">
                    <div className="w-full bg-gray-700 rounded-full h-1.5">
                      <div
                        className="bg-blue-500 h-1.5 rounded-full"
                        style={{ width: `${badge.progress || 0}%` }}
                      />
                    </div>
                    <p className="text-gray-500 text-xs text-center mt-1">
                      {badge.progress || 0}% complete
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Recent Achievements */}
      <div className="card">
        <h3 className="text-xl font-bold text-white mb-4">Recent Achievements</h3>
        <div className="space-y-3">
          {stats?.recentAchievements?.map((achievement, idx) => (
            <div key={idx} className="bg-ironcore-dark rounded-lg p-4 flex items-center gap-4">
              <div className="text-4xl">{achievement.icon}</div>
              <div className="flex-1">
                <h4 className="text-white font-semibold">{achievement.title}</h4>
                <p className="text-gray-400 text-sm">{achievement.description}</p>
              </div>
              <div className="text-right">
                <p className="text-primary-400 font-bold">+{achievement.xp} XP</p>
                <p className="text-gray-400 text-xs">{achievement.date}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
