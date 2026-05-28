import { useSocketStore } from '../store/socketStore'
import { DevicePhoneMobileIcon, CheckCircleIcon, XCircleIcon } from '@heroicons/react/24/outline'
import { formatDistanceToNow } from 'date-fns'

export default function Devices() {
  const { devices, connected } = useSocketStore()

  const onlineDevices = devices.filter(d => d.isOnline)
  const offlineDevices = devices.filter(d => !d.isOnline)

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-white">Devices</h1>
        <p className="text-gray-400 mt-1">Manage your connected devices</p>
      </div>

      <div className="card">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-semibold text-white">Connection Status</h2>
          <div className="flex items-center gap-2">
            {connected ? (
              <>
                <CheckCircleIcon className="h-5 w-5 text-green-400" />
                <span className="text-green-400 font-medium">Connected</span>
              </>
            ) : (
              <>
                <XCircleIcon className="h-5 w-5 text-red-400" />
                <span className="text-red-400 font-medium">Disconnected</span>
              </>
            )}
          </div>
        </div>

        {onlineDevices.length > 0 && (
          <div className="mb-6">
            <h3 className="text-lg font-semibold text-white mb-3">Online Devices</h3>
            <div className="space-y-3">
              {onlineDevices.map((device) => (
                <div
                  key={device.deviceId}
                  className="flex items-center gap-4 p-4 bg-gray-700 rounded-lg"
                >
                  <DevicePhoneMobileIcon className="h-8 w-8 text-green-400" />
                  <div className="flex-1">
                    <h4 className="font-semibold text-white">
                      {device.deviceName || device.deviceType}
                    </h4>
                    <p className="text-sm text-gray-400 capitalize">{device.deviceType}</p>
                  </div>
                  <span className="status-online">Online</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {offlineDevices.length > 0 && (
          <div>
            <h3 className="text-lg font-semibold text-white mb-3">Offline Devices</h3>
            <div className="space-y-3">
              {offlineDevices.map((device) => (
                <div
                  key={device.deviceId}
                  className="flex items-center gap-4 p-4 bg-gray-700/50 rounded-lg"
                >
                  <DevicePhoneMobileIcon className="h-8 w-8 text-gray-500" />
                  <div className="flex-1">
                    <h4 className="font-semibold text-gray-300">
                      {device.deviceName || device.deviceType}
                    </h4>
                    <p className="text-sm text-gray-500">
                      Last seen {formatDistanceToNow(new Date(device.lastSeen), { addSuffix: true })}
                    </p>
                  </div>
                  <span className="status-offline">Offline</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {devices.length === 0 && (
          <div className="text-center py-12">
            <DevicePhoneMobileIcon className="h-12 w-12 text-gray-600 mx-auto mb-4" />
            <p className="text-gray-400">No devices connected</p>
            <p className="text-sm text-gray-500 mt-2">
              Connect your mobile app or wearable to start syncing
            </p>
          </div>
        )}
      </div>
    </div>
  )
}
