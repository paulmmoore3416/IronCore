import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'
import {
  FireIcon,
  SparklesIcon,
  PlusIcon,
  TrashIcon,
  PencilIcon,
  ChartBarIcon,
  ClockIcon,
  CameraIcon,
  MagnifyingGlassIcon,
  ArrowPathIcon,
  BeakerIcon,
  ScaleIcon,
} from '@heroicons/react/24/outline'

const CUISINES = [
  'American', 'Italian', 'Mexican', 'Chinese', 'Japanese', 'Indian', 
  'Thai', 'Mediterranean', 'French', 'Korean', 'Vietnamese', 'Greek',
  'Spanish', 'Middle Eastern', 'Caribbean', 'Brazilian', 'German', 'British'
]

const MEAL_TIMES = [
  { id: 'breakfast', name: 'Breakfast', icon: '🌅', time: '7:00 AM' },
  { id: 'morning-snack', name: 'Morning Snack', icon: '☕', time: '10:00 AM' },
  { id: 'lunch', name: 'Lunch', icon: '🌞', time: '12:30 PM' },
  { id: 'afternoon-snack', name: 'Afternoon Snack', icon: '🍎', time: '3:00 PM' },
  { id: 'dinner', name: 'Dinner', icon: '🌙', time: '6:30 PM' },
  { id: 'evening-snack', name: 'Evening Snack', icon: '🌃', time: '8:30 PM' },
  { id: 'pre-workout', name: 'Pre-Workout', icon: '💪', time: 'Before Training' },
  { id: 'post-workout', name: 'Post-Workout', icon: '🏋️', time: 'After Training' },
]

const DIETARY_PREFERENCES = [
  'Standard', 'Vegetarian', 'Vegan', 'Keto', 'Paleo', 'Low-Carb', 
  'High-Protein', 'Gluten-Free', 'Dairy-Free', 'Pescatarian'
]

export default function Nutrition() {
  const { token } = useAuthStore()
  const queryClient = useQueryClient()
  const [selectedCuisine, setSelectedCuisine] = useState('American')
  const [selectedDiet, setSelectedDiet] = useState('Standard')
  const [isGenerating, setIsGenerating] = useState(false)
  const [showAddMeal, setShowAddMeal] = useState(false)
  const [selectedMealTime, setSelectedMealTime] = useState('breakfast')
  const [searchQuery, setSearchQuery] = useState('')

  // Fetch meals
  const { data: meals, isLoading } = useQuery({
    queryKey: ['meals'],
    queryFn: async () => {
      const response = await axios.get('/api/nutrition/meals', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.meals || []
    },
  })

  // Fetch nutrition stats
  const { data: stats } = useQuery({
    queryKey: ['nutrition-stats'],
    queryFn: async () => {
      const response = await axios.get('/api/nutrition/stats', {
        headers: { Authorization: `Bearer ${token}` },
      })
      return response.data.stats || {
        dailyCalories: 0,
        dailyProtein: 0,
        dailyCarbs: 0,
        dailyFat: 0,
        targetCalories: 2500,
        targetProtein: 150,
        targetCarbs: 250,
        targetFat: 80,
      }
    },
  })

  // Generate AI meal plan
  const generateMealPlan = useMutation({
    mutationFn: async () => {
      setIsGenerating(true)
      const response = await axios.post(
        '/api/nutrition/generate-plan',
        { cuisine: selectedCuisine, dietaryPreference: selectedDiet },
        { headers: { Authorization: `Bearer ${token}` } }
      )
      return response.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['meals'])
      queryClient.invalidateQueries(['nutrition-stats'])
      toast.success('Meal plan generated successfully!')
      setIsGenerating(false)
    },
    onError: (error) => {
      toast.error(error.response?.data?.error || 'Failed to generate meal plan')
      setIsGenerating(false)
    },
  })

  // Delete meal
  const deleteMeal = useMutation({
    mutationFn: async (mealId) => {
      await axios.delete(`/api/nutrition/meals/${mealId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['meals'])
      queryClient.invalidateQueries(['nutrition-stats'])
      toast.success('Meal deleted')
    },
  })

  // Calculate macro percentages
  const calculatePercentage = (current, target) => {
    return Math.min((current / target) * 100, 100)
  }

  // Group meals by time
  const groupedMeals = MEAL_TIMES.reduce((acc, time) => {
    acc[time.id] = meals?.filter(m => m.mealTime === time.id) || []
    return acc
  }, {})

  return (
    <div className="min-h-screen bg-ironcore-darker p-6">
      <div className="max-w-7xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-white">AI Nutrition Planner</h1>
            <p className="text-gray-400 mt-1">Personalized meal planning powered by AI</p>
          </div>
          <button
            onClick={() => setShowAddMeal(true)}
            className="btn-primary flex items-center gap-2"
          >
            <PlusIcon className="w-5 h-5" />
            Add Meal
          </button>
        </div>

        {/* Daily Macro Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="card">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <FireIcon className="w-5 h-5 text-orange-400" />
                <span className="text-gray-400 text-sm">Calories</span>
              </div>
              <span className="text-white font-bold">
                {stats?.dailyCalories || 0} / {stats?.targetCalories || 2500}
              </span>
            </div>
            <div className="w-full bg-gray-700 rounded-full h-2">
              <div
                className="bg-orange-500 h-2 rounded-full transition-all"
                style={{ width: `${calculatePercentage(stats?.dailyCalories, stats?.targetCalories)}%` }}
              />
            </div>
          </div>

          <div className="card">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <BeakerIcon className="w-5 h-5 text-blue-400" />
                <span className="text-gray-400 text-sm">Protein</span>
              </div>
              <span className="text-white font-bold">
                {stats?.dailyProtein || 0}g / {stats?.targetProtein || 150}g
              </span>
            </div>
            <div className="w-full bg-gray-700 rounded-full h-2">
              <div
                className="bg-blue-500 h-2 rounded-full transition-all"
                style={{ width: `${calculatePercentage(stats?.dailyProtein, stats?.targetProtein)}%` }}
              />
            </div>
          </div>

          <div className="card">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <ChartBarIcon className="w-5 h-5 text-green-400" />
                <span className="text-gray-400 text-sm">Carbs</span>
              </div>
              <span className="text-white font-bold">
                {stats?.dailyCarbs || 0}g / {stats?.targetCarbs || 250}g
              </span>
            </div>
            <div className="w-full bg-gray-700 rounded-full h-2">
              <div
                className="bg-green-500 h-2 rounded-full transition-all"
                style={{ width: `${calculatePercentage(stats?.dailyCarbs, stats?.targetCarbs)}%` }}
              />
            </div>
          </div>

          <div className="card">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <ScaleIcon className="w-5 h-5 text-yellow-400" />
                <span className="text-gray-400 text-sm">Fat</span>
              </div>
              <span className="text-white font-bold">
                {stats?.dailyFat || 0}g / {stats?.targetFat || 80}g
              </span>
            </div>
            <div className="w-full bg-gray-700 rounded-full h-2">
              <div
                className="bg-yellow-500 h-2 rounded-full transition-all"
                style={{ width: `${calculatePercentage(stats?.dailyFat, stats?.targetFat)}%` }}
              />
            </div>
          </div>
        </div>

        {/* AI Meal Plan Generator */}
        <div className="card bg-gradient-to-r from-purple-600 to-blue-600">
          <div className="flex items-center gap-3 mb-4">
            <SparklesIcon className="w-8 h-8 text-white" />
            <div>
              <h2 className="text-xl font-bold text-white">AI Meal Plan Generator</h2>
              <p className="text-blue-100 text-sm">Generate personalized meals based on your preferences</p>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-white text-sm font-medium mb-2">Cuisine Type</label>
              <select
                value={selectedCuisine}
                onChange={(e) => setSelectedCuisine(e.target.value)}
                className="w-full bg-white/20 text-white border border-white/30 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-white/50"
              >
                {CUISINES.map((cuisine) => (
                  <option key={cuisine} value={cuisine} className="text-gray-900">
                    {cuisine}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-white text-sm font-medium mb-2">Dietary Preference</label>
              <select
                value={selectedDiet}
                onChange={(e) => setSelectedDiet(e.target.value)}
                className="w-full bg-white/20 text-white border border-white/30 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-white/50"
              >
                {DIETARY_PREFERENCES.map((diet) => (
                  <option key={diet} value={diet} className="text-gray-900">
                    {diet}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex items-end">
              <button
                onClick={() => generateMealPlan.mutate()}
                disabled={isGenerating}
                className="w-full bg-white text-purple-600 font-semibold py-2 px-4 rounded-lg hover:bg-gray-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                {isGenerating ? (
                  <>
                    <ArrowPathIcon className="w-5 h-5 animate-spin" />
                    Generating...
                  </>
                ) : (
                  <>
                    <SparklesIcon className="w-5 h-5" />
                    Generate Plan
                  </>
                )}
              </button>
            </div>
          </div>
        </div>

        {/* Meal Timeline */}
        <div className="card">
          <h2 className="text-2xl font-bold text-white mb-6">Today's Meal Plan</h2>

          {isLoading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500"></div>
            </div>
          ) : (
            <div className="space-y-6">
              {MEAL_TIMES.map((mealTime) => {
                const timeMeals = groupedMeals[mealTime.id] || []
                
                return (
                  <div key={mealTime.id} className="border-l-4 border-primary-500 pl-6 relative">
                    <div className="absolute -left-3 top-0 w-6 h-6 bg-primary-500 rounded-full flex items-center justify-center text-sm">
                      {mealTime.icon}
                    </div>
                    
                    <div className="mb-3">
                      <h3 className="text-lg font-semibold text-white">{mealTime.name}</h3>
                      <p className="text-gray-400 text-sm">{mealTime.time}</p>
                    </div>

                    {timeMeals.length > 0 ? (
                      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {timeMeals.map((meal) => (
                          <div
                            key={meal._id}
                            className="bg-ironcore-dark rounded-lg p-4 hover:bg-gray-800 transition-colors"
                          >
                            {meal.imageUrl && (
                              <img
                                src={meal.imageUrl}
                                alt={meal.name}
                                className="w-full h-32 object-cover rounded-lg mb-3"
                              />
                            )}
                            
                            <div className="flex justify-between items-start mb-2">
                              <h4 className="text-white font-semibold">{meal.name}</h4>
                              <button
                                onClick={() => deleteMeal.mutate(meal._id)}
                                className="text-red-400 hover:text-red-300"
                              >
                                <TrashIcon className="w-4 h-4" />
                              </button>
                            </div>

                            <div className="grid grid-cols-4 gap-2 text-center text-xs">
                              <div>
                                <p className="text-gray-400">Cal</p>
                                <p className="text-white font-semibold">{meal.calories}</p>
                              </div>
                              <div>
                                <p className="text-gray-400">Pro</p>
                                <p className="text-blue-400 font-semibold">{meal.protein}g</p>
                              </div>
                              <div>
                                <p className="text-gray-400">Carb</p>
                                <p className="text-green-400 font-semibold">{meal.carbs}g</p>
                              </div>
                              <div>
                                <p className="text-gray-400">Fat</p>
                                <p className="text-yellow-400 font-semibold">{meal.fat}g</p>
                              </div>
                            </div>

                            {meal.ingredients && meal.ingredients.length > 0 && (
                              <div className="mt-3 pt-3 border-t border-gray-700">
                                <p className="text-gray-400 text-xs mb-1">Ingredients:</p>
                                <div className="flex flex-wrap gap-1">
                                  {meal.ingredients.slice(0, 3).map((ing, idx) => (
                                    <span
                                      key={idx}
                                      className="text-xs bg-gray-700 text-gray-300 px-2 py-1 rounded"
                                    >
                                      {ing}
                                    </span>
                                  ))}
                                  {meal.ingredients.length > 3 && (
                                    <span className="text-xs text-gray-400">
                                      +{meal.ingredients.length - 3} more
                                    </span>
                                  )}
                                </div>
                              </div>
                            )}
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div className="text-center py-8 bg-ironcore-dark rounded-lg">
                        <p className="text-gray-400">No meals planned for {mealTime.name.toLowerCase()}</p>
                        <button
                          onClick={() => {
                            setSelectedMealTime(mealTime.id)
                            setShowAddMeal(true)
                          }}
                          className="mt-2 text-primary-500 hover:text-primary-400 text-sm font-medium"
                        >
                          Add a meal
                        </button>
                      </div>
                    )}
                  </div>
                )
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}