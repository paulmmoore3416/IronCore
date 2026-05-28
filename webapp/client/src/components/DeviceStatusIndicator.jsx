import { Fragment } from 'react'
import { Menu, Transition } from '@headlessui/react'
import { 
  DevicePhoneMobileIcon, 
  SignalIcon,
  SignalSlashIcon 
} from '@heroicons/react/24/outline'
import { formatDistanceToNow } from 'date-fns'

export default function DeviceStatusIndicator({ connected, devices = [] }) {
  const onlineDevices = devices.filter(d => d.isOnline)
  const offlineDevices = devices.filter(d => !d.isOnline)

  return (
    <Menu as="div" className="relative">
      <Menu.Button className="flex items-center gap-2 rounded-lg bg-gray-800 px-3 py-2 text-sm hover:bg-gray-700 transition-colors">
        {connected ? (
          <SignalIcon className="h-5 w-5 text-green-400" />
        ) : (
          <SignalSlashIcon className="h-5 w-5 text-red-400" />
        )}
        <span className="text-gray-300">
          {onlineDevices.length} Device{onlineDevices.length !== 1 ? 's' : ''}
        </span>
      </Menu.Button>

      <Transition
        as={Fragment}
        enter="transition ease-out duration-100"
        enterFrom="transform opacity-0 scale-95"
        enterTo="transform opacity-100 scale-100"
        leave="transition ease-in duration-75"
        leaveFrom="transform opacity-100 scale-100"
        leaveTo="transform opacity-0 scale-95"
      >
        <Menu.Items className="absolute right-0 z-10 mt-2 w-72 origin-top-right rounded-md bg-gray-800 shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
          <div className="p-4">
            <h3 className="text-sm font-semibold text-gray-200 mb-3">
              Connected Devices
            </h3>
            
            {devices.length === 0 ? (
              <p className="text-sm text-gray-400">No devices connected</p>
            ) : (
              <div className="space-y-3">
                {onlineDevices.length > 0 && (
                  <div>
                    <p className="text-xs text-gray-400 mb-2">Online</p>
                    {onlineDevices.map((device) => (
                      <div
                        key={device.deviceId}
                        className="flex items-center gap-3 p-2 rounded bg-gray-700"
                      >
                        <DevicePhoneMobileIcon className="h-5 w-5 text-green-400" />
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-gray-200 truncate">
                            {device.deviceName || device.deviceType}
                          </p>
                          <p className="text-xs text-gray-400">
                            {device.deviceType}
                          </p>
                        </div>
                        <span className="status-online">Online</span>
                      </div>
                    ))}
                  </div>
                )}
                
                {offlineDevices.length > 0 && (
                  <div>
                    <p className="text-xs text-gray-400 mb-2">Offline</p>
                    {offlineDevices.map((device) => (
                      <div
                        key={device.deviceId}
                        className="flex items-center gap-3 p-2 rounded bg-gray-700/50"
                      >
                        <DevicePhoneMobileIcon className="h-5 w-5 text-gray-500" />
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-gray-300 truncate">
                            {device.deviceName || device.deviceType}
                          </p>
                          <p className="text-xs text-gray-500">
                            Last seen {formatDistanceToNow(new Date(device.lastSeen), { addSuffix: true })}
                          </p>
                        </div>
                        <span className="status-offline">Offline</span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>
        </Menu.Items>
      </Transition>
    </Menu>
  )
}
