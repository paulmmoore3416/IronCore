// Service Worker Registration
export function register() {
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      const swUrl = `${import.meta.env.BASE_URL}service-worker.js`;

      navigator.serviceWorker
        .register(swUrl)
        .then((registration) => {
          console.log('[SW] Service Worker registered:', registration);

          // Check for updates periodically
          setInterval(() => {
            registration.update();
          }, 60000); // Check every minute

          registration.onupdatefound = () => {
            const installingWorker = registration.installing;
            if (installingWorker == null) {
              return;
            }

            installingWorker.onstatechange = () => {
              if (installingWorker.state === 'installed') {
                if (navigator.serviceWorker.controller) {
                  // New update available
                  console.log('[SW] New content available; please refresh.');
                  
                  // Show update notification
                  if (window.confirm('New version available! Reload to update?')) {
                    installingWorker.postMessage({ type: 'SKIP_WAITING' });
                    window.location.reload();
                  }
                } else {
                  // Content cached for offline use
                  console.log('[SW] Content cached for offline use.');
                }
              }
            };
          };
        })
        .catch((error) => {
          console.error('[SW] Service Worker registration failed:', error);
        });

      // Handle controller change
      let refreshing = false;
      navigator.serviceWorker.addEventListener('controllerchange', () => {
        if (refreshing) return;
        refreshing = true;
        window.location.reload();
      });
    });
  }
}

export function unregister() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.ready
      .then((registration) => {
        registration.unregister();
      })
      .catch((error) => {
        console.error('[SW] Service Worker unregistration failed:', error);
      });
  }
}

// Request notification permission
export function requestNotificationPermission() {
  if ('Notification' in window && 'serviceWorker' in navigator) {
    Notification.requestPermission().then((permission) => {
      if (permission === 'granted') {
        console.log('[SW] Notification permission granted');
      }
    });
  }
}

// Subscribe to push notifications
export async function subscribeToPushNotifications() {
  if ('serviceWorker' in navigator && 'PushManager' in window) {
    try {
      const registration = await navigator.serviceWorker.ready;
      const subscription = await registration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: urlBase64ToUint8Array(
          import.meta.env.VITE_VAPID_PUBLIC_KEY || ''
        ),
      });
      
      console.log('[SW] Push subscription:', subscription);
      return subscription;
    } catch (error) {
      console.error('[SW] Push subscription failed:', error);
      return null;
    }
  }
  return null;
}

// Helper function to convert VAPID key
function urlBase64ToUint8Array(base64String) {
  const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding)
    .replace(/\-/g, '+')
    .replace(/_/g, '/');

  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);

  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
}

// Check if app is running in standalone mode (PWA)
export function isStandalone() {
  return (
    window.matchMedia('(display-mode: standalone)').matches ||
    window.navigator.standalone === true
  );
}

// Check if device is online
export function isOnline() {
  return navigator.onLine;
}

// Listen for online/offline events
export function setupConnectivityListeners(onOnline, onOffline) {
  window.addEventListener('online', () => {
    console.log('[SW] App is online');
    if (onOnline) onOnline();
  });

  window.addEventListener('offline', () => {
    console.log('[SW] App is offline');
    if (onOffline) onOffline();
  });
}

// Background sync for offline data
export async function registerBackgroundSync(tag) {
  if ('serviceWorker' in navigator && 'sync' in ServiceWorkerRegistration.prototype) {
    try {
      const registration = await navigator.serviceWorker.ready;
      await registration.sync.register(tag);
      console.log(`[SW] Background sync registered: ${tag}`);
      return true;
    } catch (error) {
      console.error('[SW] Background sync registration failed:', error);
      return false;
    }
  }
  return false;
}

// Cache workout data for offline sync
export async function cacheWorkoutForSync(workout, token) {
  if ('caches' in window) {
    try {
      const cache = await caches.open('offline-workouts');
      const request = new Request(`/offline-workout-${Date.now()}`);
      const response = new Response(JSON.stringify({ workout, token }), {
        headers: { 'Content-Type': 'application/json' },
      });
      await cache.put(request, response);
      await registerBackgroundSync('sync-workouts');
      console.log('[SW] Workout cached for offline sync');
      return true;
    } catch (error) {
      console.error('[SW] Failed to cache workout:', error);
      return false;
    }
  }
  return false;
}

// Cache nutrition data for offline sync
export async function cacheNutritionForSync(meal, token) {
  if ('caches' in window) {
    try {
      const cache = await caches.open('offline-nutrition');
      const request = new Request(`/offline-meal-${Date.now()}`);
      const response = new Response(JSON.stringify({ meal, token }), {
        headers: { 'Content-Type': 'application/json' },
      });
      await cache.put(request, response);
      await registerBackgroundSync('sync-nutrition');
      console.log('[SW] Nutrition data cached for offline sync');
      return true;
    } catch (error) {
      console.error('[SW] Failed to cache nutrition data:', error);
      return false;
    }
  }
  return false;
}

// Cache metrics for offline sync
export async function cacheMetricsForSync(metrics, token) {
  if ('caches' in window) {
    try {
      const cache = await caches.open('offline-metrics');
      const request = new Request(`/offline-metrics-${Date.now()}`);
      const response = new Response(JSON.stringify({ metrics, token }), {
        headers: { 'Content-Type': 'application/json' },
      });
      await cache.put(request, response);
      await registerBackgroundSync('sync-metrics');
      console.log('[SW] Metrics cached for offline sync');
      return true;
    } catch (error) {
      console.error('[SW] Failed to cache metrics:', error);
      return false;
    }
  }
  return false;
}

// Clear all caches
export async function clearAllCaches() {
  if ('caches' in window) {
    try {
      const cacheNames = await caches.keys();
      await Promise.all(cacheNames.map((name) => caches.delete(name)));
      console.log('[SW] All caches cleared');
      return true;
    } catch (error) {
      console.error('[SW] Failed to clear caches:', error);
      return false;
    }
  }
  return false;
}
