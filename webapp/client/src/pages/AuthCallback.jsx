import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import toast from 'react-hot-toast'

export default function AuthCallback() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { setToken } = useAuthStore()

  useEffect(() => {
    const token = searchParams.get('token')
    
    if (token) {
      setToken(token)
      toast.success('Successfully logged in with Google!')
      navigate('/')
    } else {
      toast.error('Authentication failed')
      navigate('/login')
    }
  }, [searchParams, setToken, navigate])

  return (
    <div className="min-h-screen flex items-center justify-center bg-ironcore-darker">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500 mx-auto"></div>
        <p className="mt-4 text-gray-400">Completing authentication...</p>
      </div>
    </div>
  )
}
