# IronCore Metrics - Implementation Plan
**Date:** May 27, 2026  
**Status:** Ready for Code Mode Implementation  
**Estimated Time:** 12-16 hours for critical fixes

---

## 🎯 Immediate Critical Fixes (Must Complete Before Build)

### Fix 1: NutritionViewModel Database Persistence
**File:** `app/src/main/java/com/ironcore/metrics/ui/nutrition/NutritionViewModel.kt`  
**Priority:** CRITICAL  
**Time:** 2 hours

**Problem:**
```kotlin
// Current: Meals only stored in StateFlow
_meals.value = generatedMeals
// Lost on app restart!
```

**Solution:**
```kotlin
// After line where _meals.value is set, add:
viewModelScope.launch {
    _meals.value.forEach { remoteMeal ->
        val meal = Meal(
            name = remoteMeal.name,
            totalCalories = remoteMeal.calories,
            protein = remoteMeal.protein,
            carbs = remoteMeal.carbs,
            fat = remoteMeal.fat,
            timestamp = System.currentTimeMillis()
        )
        nutritionDao.insertMeal(meal)
    }
}

// Also load existing meals on init:
init {
    viewModelScope.launch {
        nutritionDao.getAllMeals().collect { dbMeals ->
            if (_meals.value.isEmpty() && dbMeals.isNotEmpty()) {
                _meals.value = dbMeals.map { meal ->
                    RemoteMeal(
                        name = meal.name,
                        calories = meal.totalCalories,
                        protein = meal.protein,
                        carbs = meal.carbs,
                        fat = meal.fat,
                        ingredients = emptyList()
                    )
                }
            }
        }
    }
}
```

**Testing:**
1. Generate meal plan
2. Close app completely
3. Reopen app
4. Navigate to Nutrition screen
5. Verify meals are still displayed

---

### Fix 2: MetricDetailScreen Save Functionality
**File:** `app/src/main/java/com/ironcore/metrics/ui/navigation/MetricDetailScreen.kt`  
**Priority:** CRITICAL  
**Time:** 3 hours

**Problem:**
```kotlin
Button(onClick = { onBack() }) {
    Text("SAVE ENTRY")
}
// Does nothing with inputValue!
```

**Solution:**
Create a new ViewModel for MetricDetailScreen:

**New File:** `app/src/main/java/com/ironcore/metrics/ui/navigation/MetricDetailViewModel.kt`
```kotlin
@HiltViewModel
class MetricDetailViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val nutritionDao: NutritionDao,
    private val healthDataValidator: HealthDataValidator
) : ViewModel() {

    fun saveMetric(metricType: String, value: String) {
        viewModelScope.launch {
            try {
                when (metricType.lowercase()) {
                    "weight" -> {
                        val weightKg = value.toFloatOrNull()
                        if (weightKg != null && healthDataValidator.validateWeight(weightKg.toDouble()) > 0) {
                            healthConnectManager.writeWeight(weightKg)
                        }
                    }
                    "hydration" -> {
                        val ml = value.toIntOrNull()
                        if (ml != null && healthDataValidator.validateHydration(ml) > 0) {
                            nutritionDao.insertHydration(HydrationLog(amountMl = ml))
                        }
                    }
                    "nutrition" -> {
                        val calories = value.toIntOrNull()
                        if (calories != null && calories > 0) {
                            nutritionDao.insertMeal(
                                Meal(
                                    name = "Quick Entry",
                                    totalCalories = calories,
                                    protein = 0f,
                                    carbs = 0f,
                                    fat = 0f
                                )
                            )
                        }
                    }
                    // Add other metric types as needed
                }
            } catch (e: Exception) {
                Log.e("MetricDetailViewModel", "Error saving metric", e)
            }
        }
    }
}
```

**Update MetricDetailScreen.kt:**
```kotlin
@Composable
fun MetricDetailScreen(
    metricType: String,
    onBack: () -> Unit,
    viewModel: MetricDetailViewModel = hiltViewModel()
) {
    // ... existing code ...
    
    Button(
        onClick = { 
            viewModel.saveMetric(metricType, inputValue)
            onBack()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = themeColor)
    ) {
        Text("SAVE ENTRY", color = Color.Black)
    }
}
```

**Testing:**
1. Navigate to any metric detail screen
2. Enter a value
3. Click SAVE ENTRY
4. Return to dashboard
5. Verify metric updated

---

### Fix 3: Comprehensive Input Validation
**Files:** Multiple screens  
**Priority:** HIGH  
**Time:** 4 hours

**Locations to Fix:**

#### WorkoutLoggerScreen.kt
```kotlin
// Before viewModel.addSet(), add validation:
val validatedWeight = weight.toFloatOrNull()
val validatedReps = reps.toIntOrNull()
val validatedRpe = rpe.toIntOrNull()

if (validatedWeight == null || validatedWeight <= 0) {
    // Show error toast
    return@IronCoreButton
}
if (validatedReps == null || validatedReps <= 0 || validatedReps > 100) {
    // Show error toast
    return@IronCoreButton
}
if (validatedRpe != null && (validatedRpe < 1 || validatedRpe > 10)) {
    // Show error toast
    return@IronCoreButton
}

viewModel.addSet(
    exerciseId = 1,
    reps = validatedReps,
    weight = validatedWeight,
    rpe = validatedRpe,
    notes = if (notes.isEmpty()) null else notes
)
```

#### SettingsScreen.kt
```kotlin
// In save profile button:
Button(
    onClick = {
        val validatedAge = age.toIntOrNull()
        val validatedWeight = weight.toFloatOrNull()
        val validatedHeight = height.toFloatOrNull()
        
        if (name.isBlank()) {
            // Show error: Name required
            return@Button
        }
        if (validatedAge == null || validatedAge < 13 || validatedAge > 120) {
            // Show error: Invalid age
            return@Button
        }
        if (validatedWeight == null || validatedWeight < 30 || validatedWeight > 300) {
            // Show error: Invalid weight
            return@Button
        }
        if (validatedHeight == null || validatedHeight < 100 || validatedHeight > 250) {
            // Show error: Invalid height
            return@Button
        }
        
        viewModel.updateProfile(
            name,
            validatedAge,
            validatedWeight,
            validatedHeight,
            "Maintenance"
        )
    },
    modifier = Modifier.align(Alignment.End)
) {
    Text(stringResource(R.string.save_profile))
}
```

**Testing:**
1. Try entering invalid values (negative, zero, extremely high)
2. Verify error messages appear
3. Verify valid values save correctly

---

### Fix 4: Add Missing Meal Entity Field
**File:** `app/src/main/java/com/ironcore/metrics/data/local/entities/Meal.kt`  
**Priority:** MEDIUM  
**Time:** 30 minutes

**Problem:** Meal entity might be missing timestamp field

**Solution:**
```kotlin
@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val totalCalories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val timestamp: Long = System.currentTimeMillis() // Add if missing
)
```

---

## 🔧 Build & Deployment Steps

### Step 1: Clean Build
```bash
cd /home/paul/IronCoreMetrics
./gradlew clean
./gradlew assembleDebug
```

**Expected Output:**
```
BUILD SUCCESSFUL in Xs
```

### Step 2: Install to Device
```bash
# Check connected devices
adb devices

# Install APK
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Alternative (if gradlew install works):**
```bash
./gradlew installDebug
```

### Step 3: Manual Testing Checklist
- [ ] App launches without crash
- [ ] Dashboard displays health metrics
- [ ] Navigate to Workout screen
- [ ] Log a workout set (verify it saves)
- [ ] Navigate to Nutrition screen
- [ ] Generate AI meal plan (verify it persists)
- [ ] Navigate to Settings
- [ ] Update profile (verify it saves)
- [ ] Log hydration (verify it saves)
- [ ] Close app completely
- [ ] Reopen app
- [ ] Verify all data persisted

### Step 4: Git Commit & Push
```bash
# Stage changes
git add .

# Commit with descriptive message
git commit -m "fix: critical data persistence issues

- Add database persistence for NutritionViewModel meal plans
- Implement MetricDetailScreen save functionality with validation
- Add comprehensive input validation across all forms
- Ensure all user data persists across app restarts

Resolves production readiness blockers identified in audit"

# Push to repository
git push origin main
```

**Pre-Push Verification:**
```bash
# Verify no secrets in staged files
git diff --cached | grep -i "api_key\|secret\|password\|token"
# Should return nothing

# Verify .gitignore is working
git status --ignored
```

---

## 📊 Success Criteria

### Critical Fixes Complete When:
- ✅ Meal plans persist across app restarts
- ✅ MetricDetailScreen saves user input to database
- ✅ All forms validate input before saving
- ✅ No crashes during normal usage flow
- ✅ App builds successfully
- ✅ App installs on device
- ✅ All manual tests pass
- ✅ Changes committed and pushed to repository

### Verification Commands:
```bash
# Check database after app usage
adb shell "run-as com.ironcore.metrics ls -la /data/data/com.ironcore.metrics/databases/"

# Pull database for inspection (optional)
adb shell "run-as com.ironcore.metrics cat /data/data/com.ironcore.metrics/databases/ironcore_db" > ironcore_db.db

# Check app logs
adb logcat | grep "IronCore"
```

---

## 🚀 Next Steps After Critical Fixes

### Phase 2: Enhanced Features (Optional)
1. Implement Voice Command Workout Logging (Enhancement #1)
2. Add Smart Notifications (Enhancement #3)
3. Expand Wearable Integration (Enhancement #7)

### Phase 3: Testing & QA
1. Add unit tests for ViewModels
2. Add integration tests for database operations
3. Perform accessibility audit
4. Add crash reporting (Firebase Crashlytics)

### Phase 4: Production Release
1. Create release build configuration
2. Generate signed APK
3. Test on multiple devices
4. Submit to Play Store (if applicable)

---

## 📝 Notes

- All fixes maintain existing architecture patterns
- No breaking changes to public APIs
- Backward compatible with existing data
- Security audit passed - no secrets exposed
- Ready for code mode implementation

**Estimated Total Time:** 12-16 hours for all critical fixes + build + testing

---

**Plan Created:** 2026-05-27  
**Ready for Implementation:** YES  
**Next Action:** Switch to code mode and begin Fix #1
