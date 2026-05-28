import { create } from 'zustand'
import { io } from 'socket.io-client'

export const useSocketStore = create((set, get) => ({
  socket: null,
  connected: false,
  devices: [],
  lastSync: null,

  connect: (userId, token) => {
    const { socket } = get()
    
    if (socket?.connected) {
      return
    }

    const newSocket = io('http://localhost:5000', {
      auth: { token },
      reconnection: true,
      reconnectionAttempts: 5,
      reconnectionDelay: 1000,
    })

    newSocket.on('connect', () => {
      console.log('Socket connected:', newSocket.id)
      set({ connected: true })
      
      // Authenticate
      newSocket.emit('authenticate', {
        userId,
        deviceId: 'webapp',
        deviceType: 'webapp',
        deviceName: 'Web Dashboard',
      })
    })

    newSocket.on('authenticated', (data) => {
      console.log('Authenticated:', data)
    })

    newSocket.on('device_status_changed', (data) => {
      console.log('Device status changed:', data)
      set({ devices: data.devices })
    })

    newSocket.on('metrics_updated', (data) => {
      console.log('Metrics updated:', data)
      set({ lastSync: data.timestamp })
    })

    newSocket.on('metric_received', (data) => {
      console.log('Real-time metric:', data)
    })

    newSocket.on('disconnect', () => {
      console.log('Socket disconnected')
      set({ connected: false })
    })

    newSocket.on('connect_error', (error) => {
      console.error('Socket connection error:', error)
    })

    // Heartbeat
    const heartbeatInterval = setInterval(() => {
      if (newSocket.connected) {
        newSocket.emit('heartbeat')
      }
    }, 30000)

    newSocket.on('disconnect', () => {
      clearInterval(heartbeatInterval)
    })

    set({ socket: newSocket })
  },

  disconnect: () => {
    const { socket } = get()
    if (socket) {
      socket.disconnect()
      set({ socket: null, connected: false, devices: [] })
    }
  },

  getDeviceStatus: () => {
    const { socket } = get()
    if (socket?.connected) {
      socket.emit('get_device_status')
    }
  },

  syncMetrics: (metrics) => {
    const { socket } = get()
    if (socket?.connected) {
      socket.emit('sync_metrics', { metrics })
    }
  },
}))
