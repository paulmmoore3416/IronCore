# Health Connect Setup Instructions

## Issue: Health Data Not Loading

If you see the "Health Connect Access Required" banner even after clicking "Grant Permissions", it means Health Connect permissions need to be granted through the Health Connect app.

## Step-by-Step Setup

### 1. Install Health Connect (if not already installed)
- Open Google Play Store
- Search for "Health Connect"
- Install the official Google Health Connect app

### 2. Grant IronCore Metrics Access to Health Connect

**Option A: Through IronCore Metrics App**
1. Open IronCore Metrics
2. Click "Grant Permissions" button on dashboard
3. You'll be redirected to Health Connect app
4. In Health Connect, find "IronCore Metrics" in the app list
5. Grant ALL of the following permissions:
   - ✅ Steps (Read & Write)
   - ✅ Heart Rate (Read & Write)
   - ✅ Weight (Read & Write)
   - ✅ Active Calories Burned (Read & Write)
   - ✅ Exercise Sessions (Read)
   - ✅ Sleep (Read)
   - ✅ Hydration (Read)
   - ✅ Oxygen Saturation (Read)
   - ✅ Respiratory Rate (Read)

**Option B: Directly Through Health Connect App**
1. Open the Health Connect app
2. Tap "App permissions" or "Connected apps"
3. Find "IronCore Metrics" in the list
4. Tap on it
5. Enable ALL data types listed above

### 3. Grant Runtime Permissions

After granting Health Connect permissions, IronCore Metrics will also request:
- 📍 Location (for outdoor workout tracking)
- 🎤 Microphone (for voice commands)
- 📷 Camera (for AR body scanning)
- 🔔 Notifications (for workout reminders)
- 🔵 Bluetooth (for wearable devices)
- ❤️ Body Sensors (for heart rate monitors)
- 🏃 Activity Recognition (for automatic workout detection)

Grant these as needed for the features you want to use.

### 4. Verify Setup

1. Return to IronCore Metrics app
2. The "Health Connect Access Required" banner should disappear
3. You should see your health metrics loading:
   - Steps count
   - Heart rate (BPM)
   - Weight
   - Active calories burned
   - Other metrics

### 5. Troubleshooting

**If metrics still don't load:**

1. **Force close and restart IronCore Metrics**:
   - Settings → Apps → IronCore Metrics → Force Stop
   - Reopen the app

2. **Verify Health Connect has data**:
   - Open Health Connect app
   - Check if you have data for steps, heart rate, etc.
   - If no data, Health Connect needs to sync with your fitness devices first

3. **Check Health Connect sync**:
   - Make sure your fitness tracker (Fitbit, Garmin, etc.) is syncing to Health Connect
   - Or use Google Fit to populate Health Connect with data

4. **Re-grant permissions**:
   - Open Health Connect app
   - Find IronCore Metrics
   - Revoke all permissions
   - Go back to IronCore Metrics and click "Grant Permissions" again

## Current Status

Based on logs, IronCore Metrics currently has:
- ✅ 2 Health Connect permissions granted
- ❌ 11 Health Connect permissions missing

**Missing permissions:**
- Steps (Read & Write)
- Heart Rate (Write)
- Weight (Read & Write)
- Active Calories Burned (Read & Write)
- Exercise Sessions (Read)
- Sleep (Read)
- Hydration (Read)
- Oxygen Saturation (Read)
- Respiratory Rate (Read)

## Why This Happens

Health Connect uses a separate permission system from Android's standard runtime permissions. Even though you granted permissions through the Android system dialogs, Health Connect requires explicit permission grants through its own app interface. This is a security feature to give users fine-grained control over health data access.
