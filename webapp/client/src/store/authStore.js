import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import axios from 'axios'

const API_URL = '/api'

export const useAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      loading: false,
      error: null,

      login: async (email, password) => {
        set({ loading: true, error: null })
        try {
          const response = await axios.post(`${API_URL}/auth/login`, {
            email,
            password,
          })
          
          const { user, token } = response.data
          
          // Set state synchronously
          set({
            user,
            token,
            loading: false,
            error: null
          })
          
          console.log('Auth state updated:', { user, token: token ? 'present' : 'missing' })
          
          return response.data
        } catch (error) {
          const errorMessage = error.response?.data?.error || 'Login failed'
          set({
            error: errorMessage,
            loading: false,
            user: null,
            token: null
          })
          throw error
        }
      },

      register: async (email, password, name) => {
        set({ loading: true, error: null })
        try {
          const response = await axios.post(`${API_URL}/auth/register`, {
            email,
            password,
            name,
          })
          
          const { user, token } = response.data
          
          set({
            user,
            token,
            loading: false,
            error: null
          })
          
          return response.data
        } catch (error) {
          const errorMessage = error.response?.data?.error || 'Registration failed'
          set({
            error: errorMessage,
            loading: false,
            user: null,
            token: null
          })
          throw error
        }
      },

      logout: () => {
        set({ user: null, token: null, error: null })
        localStorage.removeItem('auth-storage')
      },

      checkAuth: async () => {
        const { token } = get()
        if (!token) {
          console.log('No token found in checkAuth')
          return
        }

        try {
          const response = await axios.get(`${API_URL}/auth/me`, {
            headers: { Authorization: `Bearer ${token}` },
          })
          set({ user: response.data.user })
          console.log('Auth check successful:', response.data.user)
        } catch (error) {
          console.error('Auth check failed:', error)
          set({ user: null, token: null })
          localStorage.removeItem('auth-storage')
        }
      },

      updateProfile: async (updates) => {
        const { token } = get()
        try {
          const response = await axios.put(`${API_URL}/auth/profile`, updates, {
            headers: { Authorization: `Bearer ${token}` },
          })
          set({ user: response.data.user })
          return response.data
        } catch (error) {
          throw error
        }
      },

      setToken: (token) => {
        set({ token })
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ 
        user: state.user, 
        token: state.token 
      }),
    }
  )
)