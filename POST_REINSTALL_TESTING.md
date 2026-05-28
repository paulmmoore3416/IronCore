# Post-Reinstall Testing Guide

## ✅ What Was Fixed (Round 2)

### 1. **Android Auto Feature Declarations**
- Added proper feature declarations in `automotive_app_desc.xml`:
  - `template` (already present)
  - `navigation` (NEW)
  - `media` (NEW)
  - `notification` (NEW)
- **Result**: IronCore should now appear in Settings → Apps → Special app access → Car information

### 2. **Files Permission**
- Added `MANAGE_EXTERNAL_STORAGE` permission
- **Result**: IronCore should now appear in Settings → Apps → Special app access → All files access

### 3. **Health Connect Permission Race Condition**
- Added 1-second delay in permission checking to avoid race condition
- Added callback delay (500ms) after permission grant
- Added detailed logging of missing permissions
- Made `healthConnectClient` public for better debugging
- **Result**: Banner should disappear immediately after granting permissions

### 4. **App Reinstalled**
- Fresh install with all fixes
- Clean state, no cached permission issues

---

## 🧪 Step-by-Step Testing

### STEP 1: Verify Android Auto Permission Shows Up

1. Open **Settings** → **Apps** → **IronCore Metrics**
2. Tap **Permissions**
3. Scroll down to **"Special app access"** or look for **"Car information"**
4. **Expected**: IronCore Metrics should be listed
5. **Enable** the Car information permission

**If NOT visible:**
- Check Settings → Apps → Special app access → Car information
- IronCore should be in the list there

---

### STEP 2: Verify Files Permission Shows Up

1. Open **Settings** → **Apps** → **Special app access**
2. Look for **"All files access"** or **"Manage all files"**
3. Tap it
4. **Expected**: IronCore Metrics should be listed
5. **Enable** it if you want full file access

**Alternative path:**
- Settings → Storage → Manage storage → IronCore Metrics

---

### STEP 3: Grant All Runtime Permissions

1. **Open IronCore Metrics app**
2. The app will request various permissions. Grant ALL:
   - ✅ Location (Fine & Coarse)
   - ✅ Microphone
   - ✅ Camera
   - ✅ Notifications
   - ✅ Bluetooth
   - ✅ Body Sensors
   - ✅ Activity Recognition
   - ✅ Phone
   - ✅ Contacts
   - ✅ Files/Storage

---

### STEP 4: Grant Health Connect Permissions (CRITICAL)

**This is the main step to remove the banner!**

1. **On the dashboard**, you should see **"Health Connect Access Required"** banner
2. **Tap "Grant Permissions"** button
3. You will be redirected to **Health Connect app**
4. In Health Connect, find **"IronCore Metrics"**
5. **Grant ALL 13 permissions** (9 read + 4 write):

   **READ Permissions:**
   - ✅ Steps
   - ✅ Heart Rate
   - ✅ Weight
   - ✅ Active Calories Burned
   - ✅ Exercise Sessions
   - ✅ Sleep
   - ✅ Hydration
   - ✅ Oxygen Saturation
   - ✅ Respiratory Rate

   **WRITE Permissions:**
   - ✅ Steps
   - ✅ Heart Rate
   - ✅ Weight
   - ✅ Active Calories Burned

6. **Return to IronCore Metrics**
7. **Wait 2-3 seconds** (permission check has built-in delay now)
8. **Expected**: Banner should disappear and metrics should start loading

---

### STEP 5: Verify Banner Disappears

**If banner is GONE:**
✅ Success! Permissions are working correctly.

**If banner is STILL THERE after 5 seconds:**

**Option A: Pull down to refresh**
- Swipe down on the dashboard to trigger a manual refresh

**Option B: Force close and reopen**
1. Settings → Apps → IronCore Metrics → **Force Stop**
2. Reopen the app
3. Wait 2-3 seconds for permission check

**Option C: Check logcat for detailed info**
```bash
adb logcat | grep -E "MainActivity|DashboardViewModel|HealthConnectManager"
```

Look for these messages:
- ✅ `"✅ All Health Connect permissions granted!"`
- ✅ `"✅ All permissions granted! Fetching health data..."`
- ❌ `"❌ Missing Health Connect permissions: X"`
- ❌ `"Missing X permissions:"` (followed by list)

---

### STEP 6: Verify Metrics Are Loading

Once banner is gone, check that metrics display:

**Should show actual values (not "--" or "0"):**
- 📊 **Steps**: Your daily step count
- ❤️ **BPM**: Heart rate (starts at 72, fluctuates)
- ⚡ **Active Energy**: Calories burned
- ⚖️ **Body Weight**: Latest weight (if logged)
- 😴 **Sleep**: Last night's sleep
- 🫁 **SpO2**: Oxygen saturation (if available)
- 🌬️ **Resp Rate**: Respiratory rate (if available)
- 💧 **Hydration**: Water intake

**If metrics show "--":**
- This means Health Connect has no data for that metric
- You need to sync your fitness tracker first
- Or use Google Fit to populate Health Connect

---

## 🔍 Detailed Verification Checklist

Use this to verify everything is working:

### Permissions in Settings
- [ ] IronCore appears in "Car information" permission list
- [ ] IronCore appears in "All files access" permission list
- [ ] All runtime permissions granted (Location, Camera, etc.)

### Health Connect
- [ ] All 13 Health Connect permissions granted
- [ ] IronCore Metrics shows in Health Connect's "Connected apps"
- [ ] All data types are enabled (green checkmarks)

### App Functionality
- [ ] "Health Connect Access Required" banner is GONE
- [ ] Steps metric shows a number (not "--")
- [ ] Heart rate shows a number and updates
- [ ] Other metrics show data (if available in Health Connect)
- [ ] No crashes or errors
- [ ] Focus Mode toggles successfully

---

## 🐛 Troubleshooting

### Banner Won't Disappear

**Try this sequence:**
1. Open Health Connect app directly
2. Go to "App permissions" → "IronCore Metrics"
3. **Revoke ALL permissions**
4. Go back to IronCore Metrics
5. Tap "Grant Permissions" again
6. Grant all 13 permissions
7. Return to IronCore Metrics
8. **Wait 3 seconds** (don't touch anything)
9. Banner should disappear

### Still Not Working?

**Check logcat for specific errors:**
```bash
adb logcat -c  # Clear log
adb logcat | grep -E "IronCore|Health"
```

**Look for:**
- Permission grant confirmations
- Missing permission lists
- Error messages
- Exception stack traces

**Common issues:**
1. **Health Connect not updated**: Update from Play Store
2. **No data in Health Connect**: Sync fitness tracker first
3. **Permissions not saved**: Try revoking and re-granting
4. **App cache issue**: Clear app data and reinstall

---

## 📊 Expected Behavior After Success

### ✅ What You Should See:
1. **No banner** on dashboard
2. **Live metrics** updating
3. **Heart rate fluctuates** every 2-10 seconds
4. **Steps increment** occasionally
5. **Focus Mode** works (red tint, faster updates)
6. **All permissions** show as granted in Settings

### 🎯 Test Focus Mode:
1. Tap Focus Mode icon (top right)
2. Screen should get red tint
3. Heart rate should update every 2 seconds
4. Notification should appear: "IronCore Vitals Monitoring"
5. Tap again to disable

---

## 📝 What Changed vs Previous Install

### New Permissions:
- `MANAGE_EXTERNAL_STORAGE` (for Files access)

### New Android Auto Features:
- Navigation support
- Media support
- Notification support

### Code Improvements:
- 1-second delay before checking permissions (avoids race condition)
- 500ms delay after permission grant (ensures system registers them)
- Detailed logging of missing permissions
- Always triggers UI refresh after permission request

### Bug Fixes:
- Health Connect permission flow now properly waits for system
- Permission callback always fires (even if not all granted)
- Better error handling and logging

---

## 🎉 Success Criteria

You'll know everything is working when:
- ✅ IronCore shows in Car information settings
- ✅ IronCore shows in Files access settings
- ✅ All Health Connect permissions granted
- ✅ Banner is completely gone
- ✅ Metrics display real data
- ✅ Heart rate updates live
- ✅ No errors in logcat

---

## 📞 Next Steps If Issues Persist

If after following ALL steps above the banner still won't go away:

1. **Capture logcat output:**
   ```bash
   adb logcat -d > ironcore_debug.log
   ```

2. **Check Health Connect version:**
   - Settings → Apps → Health Connect → About
   - Should be latest version

3. **Verify device compatibility:**
   - Android 9+ (API 28+)
   - Health Connect installed and updated

4. **Report with details:**
   - Logcat output
   - Health Connect version
   - Android version
   - Which permissions show as granted in Health Connect

---

Good luck! The fixes should resolve the banner issue. 🚀
