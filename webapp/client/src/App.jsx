import { Routes, Route, Navigate } from 'react-router-dom'
import { useEffect } from 'react'
import { useAuthStore } from './store/authStore'
import { useSocketStore } from './store/socketStore'
import Layout from './components/Layout'
import VoiceControl from './components/VoiceControl'
import Dashboard from './pages/Dashboard'
import Metrics from './pages/Metrics'
import Nutrition from './pages/Nutrition'
import Workouts from './pages/Workouts'
import Devices from './pages/Devices'
import Settings from './pages/Settings'
import Social from './pages/Social'
import TrainingPlans from './pages/TrainingPlans'
import Recovery from './pages/Recovery'
import AICoach from './pages/AICoach'
import Progress from './pages/Progress'
import Login from './pages/Login'
import Register from './pages/Register'
import AuthCallback from './pages/AuthCallback'

function App() {
  const { user, token, checkAuth } = useAuthStore()
  const { connect, disconnect } = useSocketStore()

  useEffect(() => {
    checkAuth()
  }, [checkAuth])

  useEffect(() => {
    if (user && token) {
      connect(user.id, token)
    } else {
      disconnect()
    }

    return () => disconnect()
  }, [user, token, connect, disconnect])

  if (!user) {
    return (
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/auth/callback" element={<AuthCallback />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    )
  }

  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/metrics" element={<Metrics />} />
        <Route path="/nutrition" element={<Nutrition />} />
        <Route path="/workouts" element={<Workouts />} />
        <Route path="/social" element={<Social />} />
        <Route path="/training" element={<TrainingPlans />} />
        <Route path="/recovery" element={<Recovery />} />
        <Route path="/ai-coach" element={<AICoach />} />
        <Route path="/progress" element={<Progress />} />
        <Route path="/devices" element={<Devices />} />
        <Route path="/settings" element={<Settings />} />
        <Route path="/auth/callback" element={<AuthCallback />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      <VoiceControl />
    </Layout>
  )
}

export default App
