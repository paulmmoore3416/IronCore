package com.ironcore.metrics.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Enhancement 4: Theme Toggle (Dark/Light Mode Preference)
 * Manages user theme preferences with persistent storage
 * Supports: Dark, Light, and System Default themes
 */
class ThemeManager(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")
        private val THEME_KEY = stringPreferencesKey("app_theme")
    }
    
    /**
     * Get current theme preference as Flow
     */
    val themePreference: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: AppTheme.DARK.name
        AppTheme.valueOf(themeName)
    }
    
    /**
     * Set theme preference
     */
    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
    
    /**
     * Toggle between Dark and Light themes
     */
    suspend fun toggleTheme(currentTheme: AppTheme) {
        val newTheme = when (currentTheme) {
            AppTheme.DARK -> AppTheme.LIGHT
            AppTheme.LIGHT -> AppTheme.DARK
            AppTheme.SYSTEM -> AppTheme.DARK // Default to dark when toggling from system
        }
        setTheme(newTheme)
    }
}

enum class AppTheme {
    DARK,
    LIGHT,
    SYSTEM;
    
    fun getDisplayName(): String = when (this) {
        DARK -> "Dark Mode"
        LIGHT -> "Light Mode"
        SYSTEM -> "System Default"
    }
    
    fun getIcon(): String = when (this) {
        DARK -> "🌙"
        LIGHT -> "☀️"
        SYSTEM -> "🔄"
    }
}
