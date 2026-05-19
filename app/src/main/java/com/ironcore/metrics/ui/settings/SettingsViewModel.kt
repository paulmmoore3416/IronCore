package com.ironcore.metrics.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironcore.metrics.data.local.dao.UserProfileDao
import com.ironcore.metrics.data.local.entities.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

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
            val hydrationGoal = (weight * 35).toInt()
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
