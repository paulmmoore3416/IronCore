# Handoff: IronCore Metrics
**Target Hardware:** Pixel 10 Pro XL, Wear OS, 2023 Hyundai Santa Fe (Android Auto)
**Status:** Alpha - Feature Complete with Critical Fixes Applied

## 🚀 Status Overview
IronCore Metrics is now a functional health ecosystem integrating Android mobile, Wear OS, and Android Auto. Critical data persistence gaps and validation issues have been resolved. The app is ready for field testing and further AI enhancement.

## ✅ Completed Tasks

### 1. Health & Nutrition Integration
- **Health Connect:** Bi-directional integration for Steps, HR, Weight, Active Calories, and Sleep Stages.
- **Persistence:** Local Room DB (`NutritionDao`) tracks consumed calories, hydration, and meal plans.
- **Validation:** `HealthDataValidator` integrated into `DashboardViewModel`, `MetricDetailViewModel`, and `SettingsViewModel`.
- **AI Meal Planner:** `NutritionViewModel` generates authentic regional meal plans (Nordic, West African, etc.) and persists them to the local database.

### 2. Vehicle Integration (Android Auto)
- **CarAppService:** Full implementation of `IronCoreCarAppService` and `IronCoreCarSession`.
- **Safety Screens:**
  - `EmergencyAlertScreen`: High-visibility UI triggered by critical vitals (HR > 160 BPM).
  - `PostWorkoutScreen`: Summary view of metrics for safe post-training review.
- **Connectivity:** Extends phone notifications to the head unit via `CarAppExtender`.

### 3. AI & Wearable Ecosystem
- **Recovery Advisor:** `RecoveryAdvisorService` uses Ollama/Granite (3b) to provide personalized recovery advice based on holistic data.
- **Wear OS Sync:** `WearDataSyncService` ensures real-time parity between phone and watch for:
  - Health Metrics (HR, Steps, Calories)
  - Hydration (Log on watch, see on phone)
  - Recovery Score & AI Advice
- **Vitals Monitoring:** `VitalsMonitorWorker` (WorkManager) provides background monitoring even when the app is closed.

## 🛠 Recent Critical Fixes (Verified)
- **Meal Plan Persistence:** Fixed issue where AI-generated meals were lost on restart; now saved to `meals` table.
- **MetricDetail Save:** Save button now correctly persists user input for weight, hydration, and nutrition.
- **Profile Validation:** Added age/weight/height constraints in `SettingsScreen` to prevent database corruption.
- **Schema Update:** Added `timestamp` and `cuisine` fields to the `Meal` entity for better historical tracking.

## 📋 Next Steps for Bob AI

### High Priority (Functional)
- [ ] **Emergency Dialer:** Implement the actual `ACTION_DIAL` intent for the "CALL EMERGENCY" button in `EmergencyAlertScreen.kt`.
- [ ] **Dynamic HR Thresholds:** Update `VitalsMonitorWorker` and `IronCoreVitalsService` to use age-calculated max HR (e.g., 220-age) instead of a hardcoded 160/180 BPM.
- [ ] **Specialized Workout Validation:** Add input validation to `EditableExerciseCard` in `SpecializedWorkoutScreen.kt`.

### Medium Priority (Polishing)
- [ ] **Voice Command Logging:** Finalize `VoiceCommandHelper` integration to allow "Log 10 reps at 225" via speech recognition.
- [ ] **UI Polish:** Add `ContentDescriptions` to all icons for Accessibility (TalkBack) compliance.
- [ ] **ProGuard Rules:** Define `proguard-rules.pro` to protect Hilt/Room/HealthConnect classes in release builds.

### Long-Term (Research)
- [ ] **Predictive Recovery AI:** Transition from heuristic-based recovery scores to TensorFlow Lite on-device inference for trend prediction.
- [ ] **AR Body Mapping:** Implement ARCore body scanning for visual progress tracking.

## 🔍 Technical Context
- **Build System:** Always use `./gradlew` from the root.
- **AI Backend:** Ollama/Homelab is expected at `http://10.0.2.2:11434` (emulator) or your specific homelab IP.
- **Key Files:**
  - `DashboardViewModel.kt`: Central hub for data orchestration.
  - `HealthConnectManager.kt`: Health data abstraction.
  - `IronCoreCarSession.kt`: Entry point for Android Auto.
  - `RecoveryAdvisorService.kt`: AI logic for recovery insights.

---
*Created by Gemini CLI Agent for Bob AI - May 28, 2026*
