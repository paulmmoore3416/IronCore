import { useState } from 'react'
import { useAuthStore } from '../store/authStore'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import toast from 'react-hot-toast'

export default function Settings() {
  const { user, token, updateProfile } = useAuthStore()
  const queryClient = useQueryClient()
  const [googleIntegrations, setGoogleIntegrations] = useState({
    calendar: user?.googleIntegrations?.calendar || false,
    drive: user?.googleIntegrations?.drive || false,
    fit: user?.googleIntegrations?.fit || false,
    keep: user?.googleIntegrations?.keep || false,
  })

  const toggleIntegration = useMutation({
    mutationFn: async ({ service, enabled }) => {
      const response = await axios.post(
        `/api/google/integrations/${service}`,
        { enabled },
        { headers: { Authorization: `Bearer ${token}` } }
      )
      return response.data
    },
    onSuccess: (data, variables) => {
      toast.success(`${variables.service} integration ${variables.enabled ? 'enabled' : 'disabled'}`)
      setGoogleIntegrations(data.integrations)
    },
    onError: () => {
      toast.error('Failed to update integration')
    },
  })

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-white">Settings</h1>
        <p className="text-gray-400 mt-1">Manage your account and preferences</p>
      </div>

      <div className="card">
        <h2 className="text-xl font-semibold text-white mb-4">Profile</h2>
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Name</label>
            <input
              type="text"
              value={user?.name || ''}
              className="input-field w-full"
              disabled
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Email</label>
            <input
              type="email"
              value={user?.email || ''}
              className="input-field w-full"
              disabled
            />
          </div>
        </div>
      </div>

      <div className="card">
        <h2 className="text-xl font-semibold text-white mb-4">Google Integrations</h2>
        <p className="text-sm text-gray-400 mb-4">
          Connect your Google services to enhance your IronCore experience
        </p>
        <div className="space-y-4">
          {Object.entries(googleIntegrations).map(([service, enabled]) => (
            <div key={service} className="flex items-center justify-between p-4 bg-gray-700 rounded-lg">
              <div>
                <h3 className="font-semibold text-white capitalize">{service}</h3>
                <p className="text-sm text-gray-400">
                  {service === 'calendar' && 'Sync workouts to your calendar'}
                  {service === 'drive' && 'Backup your data to Google Drive'}
                  {service === 'fit' && 'Import data from Google Fit'}
                  {service === 'keep' && 'Save notes and meal plans'}
                </p>
              </div>
              <button
                onClick={() => toggleIntegration.mutate({ service, enabled: !enabled })}
                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                  enabled ? 'bg-primary-600' : 'bg-gray-600'
                }`}
              >
                <span
                  className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                    enabled ? 'translate-x-6' : 'translate-x-1'
                  }`}
                />
              </button>
            </div>
          ))}
        </div>
      </div>

      <div className="card">
        <h2 className="text-xl font-semibold text-white mb-4">Preferences</h2>
        <div className="space-y-4">
          <div className="flex items-center justify-between p-4 bg-gray-700 rounded-lg">
            <div>
              <h3 className="font-semibold text-white">Dark Mode</h3>
              <p className="text-sm text-gray-400">Always enabled for optimal viewing</p>
            </div>
            <span className="status-online">Active</span>
          </div>
        </div>
      </div>
    </div>
  )
}
