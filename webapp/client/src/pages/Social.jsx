import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'
import {
  UserGroupIcon,
  TrophyIcon,
  FireIcon,
  HeartIcon,
  ChatBubbleLeftIcon,
  HandThumbUpIcon,
  ShareIcon,
  UserPlusIcon,
  BoltIcon,
  ChartBarIcon,
  SparklesIcon,
  FlagIcon,
} from '@heroicons/react/24/outline'

const CHALLENGE_TYPES = [
  { id: 'steps', name: 'Step Challenge', icon: '👣', target: 100000, unit: 'steps' },
  { id: 'workouts', name: 'Workout Streak', icon: '💪', target: 7, unit: 'days' },
  { id: 'calories', name: 'Calorie Burn', icon: '🔥', target: 20000, unit: 'cal' },
  { id: 'distance', name: 'Distance Goal', icon: '🏃', target: 50, unit: 'km' },
  { id: 'active-minutes', name: 'Active Time', icon: '⏱️', target: 300, unit: 'min' },
]

export default function Social() {
  const { token, user } = useAuthStore()
  const queryClient = useQueryClient()
  const [activeTab, setActiveTab] = useState('feed')
  const [showCreateChallenge, setShowCreateChallenge] = useState(false)

  // Fetch activity feed
  const { data: feed, isLoading: feedLoading } = useQuery({
    queryKey: ['social-feed'],
    queryFn: async () => {
      const response = await axios.get('/api/social/feed', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.activities || []
    },
  })

  // Fetch leaderboard
  const { data: leaderboard } = useQuery({
    queryKey: ['leaderboard'],
    queryFn: async () => {
      const response = await axios.get('/api/social/leaderboard', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.leaderboard || []
    },
  })

  // Fetch active challenges
  const { data: challenges } = useQuery({
    queryKey: ['challenges'],
    queryFn: async () => {
      const response = await axios.get('/api/social/challenges', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.challenges || []
    },
  })

  // Fetch friends
  const { data: friends } = useQuery({
    queryKey: ['friends'],
    queryFn: async () => {
      const response = await axios.get('/api/social/friends', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.friends || []
    },
  })

  // Like activity
  const likeActivity = useMutation({
    mutationFn: async (activityId) => {
      await axios.post(`/api/social/activities/${activityId}/like`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['social-feed'])
      toast.success('Liked!')
    },
  })

  // Join challenge
  const joinChallenge = useMutation({
    mutationFn: async (challengeId) => {
      await axios.post(`/api/social/challenges/${challengeId}/join`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['challenges'])
      toast.success('Challenge joined!')
    },
  })

  return (
    <div className="min-h-screen bg-ironcore-darker p-6">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-white">Community Hub</h1>
            <p className="text-gray-400 mt-1">Connect, compete, and achieve together</p>
          </div>
          <button className="btn-primary flex items-center gap-2">
            <UserPlusIcon className="w-5 h-5" />
            Find Friends
          </button>
        </div>

        {/* Stats Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="card bg-gradient-to-br from-blue-600 to-blue-800">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-100 text-sm mb-1">Global Rank</p>
                <p className="text-4xl font-bold text-white">#247</p>
                <p className="text-blue-200 text-xs mt-1">Top 5%</p>
              </div>
              <TrophyIcon className="w-16 h-16 text-blue-300 opacity-50" />
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-green-500/20 rounded-lg">
                <UserGroupIcon className="w-6 h-6 text-green-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Friends</p>
                <p className="text-2xl font-bold text-white">{friends?.length || 0}</p>
                <p className="text-gray-400 text-xs">12 active today</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-orange-500/20 rounded-lg">
                <FireIcon className="w-6 h-6 text-orange-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Active Challenges</p>
                <p className="text-2xl font-bold text-white">{challenges?.filter(c => c.active).length || 0}</p>
                <p className="text-gray-400 text-xs">3 ending soon</p>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-purple-500/20 rounded-lg">
                <BoltIcon className="w-6 h-6 text-purple-400" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Total Points</p>
                <p className="text-2xl font-bold text-white">12,450</p>
                <p className="text-green-400 text-xs">+250 this week</p>
              </div>
            </div>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="card">
          <div className="flex gap-4 border-b border-gray-700">
            {[
              { id: 'feed', name: 'Activity Feed', icon: ChatBubbleLeftIcon },
              { id: 'challenges', name: 'Challenges', icon: FlagIcon },
              { id: 'leaderboard', name: 'Leaderboard', icon: TrophyIcon },
              { id: 'friends', name: 'Friends', icon: UserGroupIcon },
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-4 py-3 font-medium transition-colors border-b-2 ${
                  activeTab === tab.id
                    ? 'text-primary-500 border-primary-500'
                    : 'text-gray-400 border-transparent hover:text-gray-300'
                }`}
              >
                <tab.icon className="w-5 h-5" />
                {tab.name}
              </button>
            ))}
          </div>

          <div className="p-6">
            {/* Activity Feed Tab */}
            {activeTab === 'feed' && (
              <div className="space-y-4">
                {feedLoading ? (
                  <div className="flex justify-center py-12">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div>
                  </div>
                ) : feed && feed.length > 0 ? (
                  feed.map((activity, idx) => (
                    <div key={idx} className="bg-ironcore-dark rounded-lg p-4">
                      <div className="flex items-start gap-4">
                        <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-500 rounded-full flex items-center justify-center text-white font-bold">
                          {activity.userName?.charAt(0) || 'U'}
                        </div>
                        <div className="flex-1">
                          <div className="flex items-center gap-2 mb-2">
                            <span className="text-white font-semibold">{activity.userName || 'User'}</span>
                            <span className="text-gray-400 text-sm">completed a workout</span>
                            <span className="text-gray-500 text-xs">2h ago</span>
                          </div>
                          <div className="bg-gray-800 rounded-lg p-3 mb-3">
                            <h4 className="text-white font-semibold mb-2">{activity.title || 'Morning Run'}</h4>
                            <div className="grid grid-cols-3 gap-4 text-sm">
                              <div>
                                <p className="text-gray-400">Duration</p>
                                <p className="text-white font-semibold">{activity.duration || '45 min'}</p>
                              </div>
                              <div>
                                <p className="text-gray-400">Calories</p>
                                <p className="text-orange-400 font-semibold">{activity.calories || '450'}</p>
                              </div>
                              <div>
                                <p className="text-gray-400">Distance</p>
                                <p className="text-blue-400 font-semibold">{activity.distance || '8.5 km'}</p>
                              </div>
                            </div>
                          </div>
                          <div className="flex items-center gap-4">
                            <button
                              onClick={() => likeActivity.mutate(activity.id)}
                              className="flex items-center gap-2 text-gray-400 hover:text-red-400 transition-colors"
                            >
                              <HeartIcon className="w-5 h-5" />
                              <span className="text-sm">{activity.likes || 0}</span>
                            </button>
                            <button className="flex items-center gap-2 text-gray-400 hover:text-blue-400 transition-colors">
                              <ChatBubbleLeftIcon className="w-5 h-5" />
                              <span className="text-sm">{activity.comments || 0}</span>
                            </button>
                            <button className="flex items-center gap-2 text-gray-400 hover:text-green-400 transition-colors">
                              <ShareIcon className="w-5 h-5" />
                              <span className="text-sm">Share</span>
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="text-center py-12">
                    <ChatBubbleLeftIcon className="w-16 h-16 text-gray-600 mx-auto mb-4" />
                    <p className="text-gray-400">No activities yet. Start following friends to see their updates!</p>
                  </div>
                )}
              </div>
            )}

            {/* Challenges Tab */}
            {activeTab === 'challenges' && (
              <div className="space-y-4">
                <div className="flex justify-between items-center mb-4">
                  <h3 className="text-xl font-bold text-white">Active Challenges</h3>
                  <button
                    onClick={() => setShowCreateChallenge(true)}
                    className="btn-primary text-sm"
                  >
                    Create Challenge
                  </button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {CHALLENGE_TYPES.map((challenge) => (
                    <div key={challenge.id} className="bg-ironcore-dark rounded-lg p-4 border-2 border-gray-700 hover:border-primary-500 transition-colors">
                      <div className="flex items-start justify-between mb-3">
                        <div className="flex items-center gap-3">
                          <div className="text-4xl">{challenge.icon}</div>
                          <div>
                            <h4 className="text-white font-semibold">{challenge.name}</h4>
                            <p className="text-gray-400 text-sm">7 days remaining</p>
                          </div>
                        </div>
                        <span className="px-3 py-1 bg-green-500/20 text-green-400 text-xs font-semibold rounded-full">
                          Active
                        </span>
                      </div>

                      <div className="mb-3">
                        <div className="flex justify-between text-sm mb-1">
                          <span className="text-gray-400">Progress</span>
                          <span className="text-white font-semibold">
                            {Math.floor(Math.random() * challenge.target)} / {challenge.target} {challenge.unit}
                          </span>
                        </div>
                        <div className="w-full bg-gray-700 rounded-full h-2">
                          <div
                            className="bg-gradient-to-r from-blue-500 to-purple-500 h-2 rounded-full transition-all"
                            style={{ width: `${Math.random() * 100}%` }}
                          />
                        </div>
                      </div>

                      <div className="flex items-center justify-between">
                        <div className="flex -space-x-2">
                          {[1, 2, 3, 4].map((i) => (
                            <div
                              key={i}
                              className="w-8 h-8 bg-gradient-to-br from-blue-500 to-purple-500 rounded-full border-2 border-ironcore-dark flex items-center justify-center text-white text-xs font-bold"
                            >
                              {String.fromCharCode(65 + i)}
                            </div>
                          ))}
                          <div className="w-8 h-8 bg-gray-700 rounded-full border-2 border-ironcore-dark flex items-center justify-center text-gray-400 text-xs">
                            +12
                          </div>
                        </div>
                        <button
                          onClick={() => joinChallenge.mutate(challenge.id)}
                          className="px-4 py-2 bg-primary-500 hover:bg-primary-600 text-white text-sm font-semibold rounded-lg transition-colors"
                        >
                          Join
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Leaderboard Tab */}
            {activeTab === 'leaderboard' && (
              <div className="space-y-4">
                <div className="flex gap-4 mb-6">
                  {['Weekly', 'Monthly', 'All Time'].map((period) => (
                    <button
                      key={period}
                      className="px-4 py-2 bg-gray-800 hover:bg-gray-700 text-white rounded-lg transition-colors"
                    >
                      {period}
                    </button>
                  ))}
                </div>

                <div className="space-y-2">
                  {[
                    { rank: 1, name: 'Sarah Johnson', points: 15420, avatar: 'S', trend: '+250' },
                    { rank: 2, name: 'Mike Chen', points: 14890, avatar: 'M', trend: '+180' },
                    { rank: 3, name: 'Emma Davis', points: 13560, avatar: 'E', trend: '+320' },
                    { rank: 4, name: user?.name || 'You', points: 12450, avatar: user?.name?.charAt(0) || 'Y', trend: '+250', isUser: true },
                    { rank: 5, name: 'Alex Rodriguez', points: 11230, avatar: 'A', trend: '+190' },
                  ].map((entry) => (
                    <div
                      key={entry.rank}
                      className={`flex items-center gap-4 p-4 rounded-lg transition-colors ${
                        entry.isUser
                          ? 'bg-gradient-to-r from-blue-600/20 to-purple-600/20 border-2 border-primary-500'
                          : 'bg-ironcore-dark hover:bg-gray-800'
                      }`}
                    >
                      <div className={`w-12 h-12 rounded-full flex items-center justify-center font-bold text-lg ${
                        entry.rank === 1 ? 'bg-yellow-500 text-yellow-900' :
                        entry.rank === 2 ? 'bg-gray-400 text-gray-900' :
                        entry.rank === 3 ? 'bg-orange-600 text-orange-100' :
                        'bg-gradient-to-br from-blue-500 to-purple-500 text-white'
                      }`}>
                        {entry.rank <= 3 ? '🏆' : entry.avatar}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center gap-2">
                          <span className="text-white font-semibold">{entry.name}</span>
                          {entry.isUser && (
                            <span className="px-2 py-0.5 bg-primary-500 text-white text-xs font-semibold rounded">
                              YOU
                            </span>
                          )}
                        </div>
                        <p className="text-gray-400 text-sm">{entry.points.toLocaleString()} points</p>
                      </div>
                      <div className="text-right">
                        <p className="text-2xl font-bold text-white">#{entry.rank}</p>
                        <p className="text-green-400 text-sm">{entry.trend}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Friends Tab */}
            {activeTab === 'friends' && (
              <div className="space-y-4">
                <div className="flex justify-between items-center mb-4">
                  <h3 className="text-xl font-bold text-white">Your Friends ({friends?.length || 0})</h3>
                  <button className="btn-primary text-sm flex items-center gap-2">
                    <UserPlusIcon className="w-4 h-4" />
                    Add Friends
                  </button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {[
                    { name: 'Sarah Johnson', status: 'online', lastActivity: 'Completed 5K run', avatar: 'S' },
                    { name: 'Mike Chen', status: 'offline', lastActivity: 'Gym session 2h ago', avatar: 'M' },
                    { name: 'Emma Davis', status: 'online', lastActivity: 'Yoga class', avatar: 'E' },
                    { name: 'Alex Rodriguez', status: 'offline', lastActivity: 'Cycling 5h ago', avatar: 'A' },
                    { name: 'Lisa Wang', status: 'online', lastActivity: 'Swimming', avatar: 'L' },
                    { name: 'Tom Brown', status: 'offline', lastActivity: 'Rest day', avatar: 'T' },
                  ].map((friend, idx) => (
                    <div key={idx} className="bg-ironcore-dark rounded-lg p-4 hover:bg-gray-800 transition-colors">
                      <div className="flex items-center gap-3 mb-3">
                        <div className="relative">
                          <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-500 rounded-full flex items-center justify-center text-white font-bold text-lg">
                            {friend.avatar}
                          </div>
                          <div className={`absolute bottom-0 right-0 w-3 h-3 rounded-full border-2 border-ironcore-dark ${
                            friend.status === 'online' ? 'bg-green-500' : 'bg-gray-500'
                          }`} />
                        </div>
                        <div className="flex-1">
                          <h4 className="text-white font-semibold">{friend.name}</h4>
                          <p className="text-gray-400 text-xs">{friend.lastActivity}</p>
                        </div>
                      </div>
                      <div className="flex gap-2">
                        <button className="flex-1 px-3 py-2 bg-primary-500 hover:bg-primary-600 text-white text-sm font-semibold rounded-lg transition-colors">
                          Challenge
                        </button>
                        <button className="px-3 py-2 bg-gray-700 hover:bg-gray-600 text-white text-sm rounded-lg transition-colors">
                          <ChatBubbleLeftIcon className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
