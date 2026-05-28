# Health Connect Testing Guide - Post-Fix

## ✅ Changes Applied

### 1. **Version Compatibility Fixed**
- Configured Gradle to use Java 17 (compatible with Kotlin 2.0.0)
- Removed deprecated Compose Compiler settings
- Build now succeeds without errors

### 2. **Permissions Added**
The following permissions have been added to the manifest:

**Android Auto:**
- `CAR_APP_ACCESS_SURFACE`
- `CAR_APP_LAUNCH_TEMPLATE`

**Files & Storage:**
- `READ_EXTERNAL_STORAGE` (for older devices)
- `WRITE_EXTERNAL_STORAGE` (for older devices)

**Phone:**
- `READ_PHONE_STATE`
- `READ_CALL_LOG`

**Contacts:**
- `READ_CONTACTS` (for emergency contacts)

**Music & Audio:**
- `READ_MEDIA_AUDIO`
- `MODIFY_AUDIO_SETTINGS`

### 3. **Health Connect Permission Flow Fixed**
- Simplified permission request to use proper PermissionController API
- Fixed async permission checking in DashboardViewModel
- Added better error handling and logging

### 4. **App Reinstalled**
- Latest version with all fixes has been installed on your device
- Package: `com.ironcore.metrics`

---

## 🧪 Testing Steps

### Step 1: Grant Android Auto Permissions
1. Open **Settings** → **Apps** → **IronCore Metrics**
2. Tap **Permissions**
3. Look for **Car information** or **Android Auto** section
4. Enable all Android Auto related permissions

### Step 2: Grant Runtime Permissions
When you open the app, it will request various permissions. Grant:
- ✅ **Location** (for outdoor workout tracking)
- ✅ **Microphone** (for voice commands)
- ✅ **Camera** (for AR body scanning)
- ✅ **Notifications** (for workout reminders)
- ✅ **Bluetooth** (for wearable devices)
- ✅ **Body Sensors** (for heart rate monitors)
- ✅ **Activity Recognition** (for automatic workout detection)
- ✅ **Phone** (for emergency calls)
- ✅ **Contacts** (for emergency contacts)
- ✅ **Files/Storage** (for workout photos/videos)
- ✅ **Music & Audio** (for workout music control)

### Step 3: Grant Health Connect Permissions

**CRITICAL: This is the main step to fix the "Health Connect Access Required" banner**

1. **Open IronCore Metrics app**
2. On the dashboard, you should see the **"Health Connect Access Required"** banner
3. **Tap "Grant Permissions"** button
4. You will be redirected to **Health Connect app**
5. In Health Connect, find **"IronCore Metrics"** in the app list
6. **Grant ALL of the following permissions:**
   - ✅ Steps (Read & Write)
   - ✅ Heart Rate (Read & Write)
   - ✅ Weight (Read & Write)
   - ✅ Active Calories Burned (Read & Write)
   - ✅ Exercise Sessions (Read)
   - ✅ Sleep (Read)
   - ✅ Hydration (Read)
   - ✅ Oxygen Saturation (Read)
   - ✅ Respiratory Rate (Read)

7. **Return to IronCore Metrics**
8. The banner should disappear and metrics should start loading

### Step 4: Verify Metrics Are Pulling

After granting all permissions, check the dashboard for:

**Expected Metrics:**
- 📊 **Steps**: Should show your daily step count
- ❤️ **BPM (Heart Rate)**: Should show live heart rate (may start at 72 and fluctuate)
- ⚡ **Active Energy**: Should show calories burned
- ⚖️ **Body Weight**: Should show your latest weight (if logged in Health Connect)
- 😴 **Sleep**: Should show last night's sleep duration
- 🫁 **SpO2**: Should show oxygen saturation (if available)
- 🌬️ **Resp Rate**: Should show respiratory rate (if available)
- 💧 **Hydration**: Should show daily water intake

**If metrics show "--" or "0":**
- This means Health Connect has no data for that metric
- You need to sync your fitness tracker (Fitbit, Garmin, etc.) with Health Connect first
- Or use Google Fit to populate Health Connect with data

---

## 🔍 Troubleshooting

### Banner Still Shows After Granting Permissions

**Solution 1: Force Close and Restart**
1. Settings → Apps → IronCore Metrics → **Force Stop**
2. Reopen the app
3. Wait 5-10 seconds for permission check to complete

**Solution 2: Check Logcat for Permission Status**
```bash
adb logcat | grep -E "MainActivity|DashboardViewModel|HealthConnectManager"
```

Look for these log messages:
- ✅ `"All Health Connect permissions granted!"`
- ✅ `"✅ All permissions granted! Fetching health data..."`
- ❌ `"Missing Health Connect permissions: X"`

**Solution 3: Manually Verify in Health Connect App**
1. Open **Health Connect** app
2. Tap **"App permissions"** or **"Connected apps"**
3. Find **"IronCore Metrics"**
4. Verify ALL 9 data types are enabled
5. If any are disabled, enable them

**Solution 4: Re-grant Permissions**
1. Open Health Connect app
2. Find IronCore Metrics
3. **Revoke all permissions**
4. Go back to IronCore Metrics
5. Tap **"Grant Permissions"** again
6. Grant all permissions again

### No Data in Health Connect

If Health Connect itself has no data:

1. **Install a fitness tracker app** (if not already):
   - Google Fit
   - Samsung Health
   - Fitbit
   - Garmin Connect

2. **Sync your fitness device** with the tracker app

3. **Connect tracker app to Health Connect**:
   - Open Health Connect
   - Tap "App permissions"
   - Find your fitness tracker app
   - Enable data sharing

4. **Wait for sync** (may take a few minutes)

5. **Return to IronCore Metrics** and pull down to refresh

---

## 📱 Expected Behavior After Fix

### ✅ Success Indicators:
1. **No "Health Connect Access Required" banner** on dashboard
2. **Metrics display actual values** (not "--" or "0")
3. **Heart rate updates** every 2-10 seconds (simulated live reading)
4. **Steps increment** occasionally (simulated activity)
5. **No permission errors** in logcat

### 🎯 Focus Mode Test:
1. Tap the **Focus Mode** icon (top right)
2. Heart rate should update more frequently (every 2 seconds)
3. Background color should change to red tint
4. A foreground service notification should appear

---

## 📋 Verification Checklist

Use this checklist to verify everything is working:

- [ ] App builds and installs without errors
- [ ] Android Auto permissions granted in Settings
- [ ] All runtime permissions granted
- [ ] Health Connect permissions granted (all 9 data types)
- [ ] "Health Connect Access Required" banner is GONE
- [ ] Steps metric shows a number (not "--")
- [ ] Heart rate shows a number and updates
- [ ] Active Energy shows calories burned
- [ ] Other metrics show data (if available in Health Connect)
- [ ] Focus Mode toggles successfully
- [ ] No crashes or errors

---

## 🐛 Known Issues

1. **Wear OS module won't install on phone** - This is expected. Wear module requires a Wear OS smartwatch.

2. **Some metrics show "--"** - This is normal if Health Connect has no data for that metric. Sync your fitness tracker first.

3. **Heart rate simulation** - The app includes a heart rate simulator for testing. Real heart rate data will override this when available from Health Connect.

---

## 📞 Support

If issues persist after following this guide:

1. **Check logs**: `adb logcat | grep -E "IronCore|Health"`
2. **Verify Health Connect version**: Should be latest from Play Store
3. **Check device compatibility**: Health Connect requires Android 9+ (API 28+)
4. **Report specific error messages** from logcat

---

## 🎉 Success!

Once all metrics are displaying correctly and the banner is gone, you're all set! The app will now:
- Track your health metrics in real-time
- Sync with your Wear OS device (if connected)
- Provide AI-powered recovery advice
- Monitor vitals during workouts
- Support Android Auto integration

Enjoy your IronCore Metrics experience! 💪
