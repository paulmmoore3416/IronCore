package com.ironcore.metrics.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironcore.metrics.data.auth.AuthManager
import com.ironcore.metrics.data.auth.AuthState
import com.ironcore.metrics.data.local.dao.UserProfileDao
import com.ironcore.metrics.data.local.entities.UserProfile
import com.ironcore.metrics.data.model.UnitSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val authManager: AuthManager
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    val authState: StateFlow<AuthState> = authManager.authState

    private val _unitSystem = MutableStateFlow(UnitSystem.METRIC)
    val unitSystem: StateFlow<UnitSystem> = _unitSystem.asStateFlow()

    fun signIn() {
        authManager.signIn()
    }

    fun signOut() {
        authManager.signOut()
    }

    fun toggleUnitSystem() {
        _unitSystem.value = if (_unitSystem.value == UnitSystem.METRIC) {
            UnitSystem.IMPERIAL
        } else {
            UnitSystem.METRIC
        }
    }

    init {
        viewModelScope.launch {
            userProfileDao.getUserProfile().collect { profile ->
                if (profile == null) {
                    val defaultProfile = UserProfile(
                        name = "User",
                        age = 30,
                        weightKg = 70f,
                        heightCm = 175f,
                        fitnessGoal = "Maintenance",
                        dailyCalorieTarget = 2000
                    )
                    userProfileDao.insertUserProfile(defaultProfile)
                    _userProfile.value = defaultProfile
                } else {
                    _userProfile.value = profile
                }
            }
        }
    }

    fun updateProfile(name: String, age: Int, weight: Float, height: Float, goal: String) {
        viewModelScope.launch {
            // Calculate goals dynamically
            val calorieGoal = ((10 * weight) + (6.25 * height) - (5 * age) + 5).toInt()
            
            val updatedProfile = UserProfile(
                id = 0,
                name = name,
                age = age,
                weightKg = weight,
                heightCm = height,
                fitnessGoal = goal,
                dailyCalorieTarget = calorieGoal
            )
            userProfileDao.insertUserProfile(updatedProfile)
        }
    }
}
