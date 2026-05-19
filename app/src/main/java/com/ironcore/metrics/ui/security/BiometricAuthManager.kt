package com.ironcore.metrics.ui.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Enhancement 3: Biometric Authentication for App Security
 * Provides fingerprint/face authentication for sensitive features like:
 * - Health data access
 * - Emergency SOS triggers
 * - Settings modifications
 * - Social features
 */
class BiometricAuthManager(private val context: Context) {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _isBiometricAvailable = MutableStateFlow(false)
    val isBiometricAvailable: StateFlow<Boolean> = _isBiometricAvailable.asStateFlow()
    
    init {
        checkBiometricAvailability()
    }
    
    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(context)
        _isBiometricAvailable.value = when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or 
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    /**
     * Authenticate user for sensitive operations
     * @param activity The FragmentActivity context
     * @param title Title for the biometric prompt
     * @param subtitle Optional subtitle
     * @param description Optional description
     * @param onSuccess Callback on successful authentication
     * @param onError Callback on authentication error
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String = "IronCore Authentication",
        subtitle: String = "Verify your identity",
        description: String = "Use your fingerprint or face to continue",
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        if (!_isBiometricAvailable.value) {
            onError("Biometric authentication not available")
            return
        }
        
        val executor = ContextCompat.getMainExecutor(context)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    _authState.value = AuthState.Error(errString.toString())
                    onError(errString.toString())
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    _authState.value = AuthState.Authenticated
                    onSuccess()
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    _authState.value = AuthState.Failed
                    onError("Authentication failed")
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()
        
        _authState.value = AuthState.Authenticating
        biometricPrompt.authenticate(promptInfo)
    }
    
    /**
     * Quick authentication for emergency features
     */
    fun authenticateEmergency(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        authenticate(
            activity = activity,
            title = "🚨 Emergency SOS",
            subtitle = "Verify to trigger emergency alert",
            description = "This will notify emergency contacts and services",
            onSuccess = onSuccess,
            onError = onError
        )
    }
    
    /**
     * Authentication for health data access
     */
    fun authenticateHealthData(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        authenticate(
            activity = activity,
            title = "🏥 Health Data Access",
            subtitle = "Verify to view sensitive health information",
            description = "Your health data is protected",
            onSuccess = onSuccess,
            onError = onError
        )
    }
    
    /**
     * Authentication for settings changes
     */
    fun authenticateSettings(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        authenticate(
            activity = activity,
            title = "⚙️ Settings Access",
            subtitle = "Verify to modify app settings",
            description = "Protect your preferences and data",
            onSuccess = onSuccess,
            onError = onError
        )
    }
    
    /**
     * Authentication for social features
     */
    fun authenticateSocial(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        authenticate(
            activity = activity,
            title = "👥 Social Features",
            subtitle = "Verify to access community features",
            description = "Connect securely with other athletes",
            onSuccess = onSuccess,
            onError = onError
        )
    }
    
    /**
     * Reset authentication state
     */
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
    
    /**
     * Check if device supports biometric authentication
     */
    fun getBiometricCapability(): BiometricCapability {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or 
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> 
                BiometricCapability.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> 
                BiometricCapability.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> 
                BiometricCapability.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> 
                BiometricCapability.NoneEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricCapability.SecurityUpdateRequired
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                BiometricCapability.Unsupported
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                BiometricCapability.Unknown
            else -> BiometricCapability.Unknown
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Authenticating : AuthState()
    object Authenticated : AuthState()
    object Failed : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class BiometricCapability {
    object Available : BiometricCapability()
    object NoHardware : BiometricCapability()
    object HardwareUnavailable : BiometricCapability()
    object NoneEnrolled : BiometricCapability()
    object SecurityUpdateRequired : BiometricCapability()
    object Unsupported : BiometricCapability()
    object Unknown : BiometricCapability()
    
    fun getMessage(): String = when (this) {
        is Available -> "Biometric authentication is available"
        is NoHardware -> "This device doesn't have biometric hardware"
        is HardwareUnavailable -> "Biometric hardware is currently unavailable"
        is NoneEnrolled -> "No biometric credentials enrolled. Please set up fingerprint or face unlock in device settings"
        is SecurityUpdateRequired -> "Security update required for biometric authentication"
        is Unsupported -> "Biometric authentication is not supported"
        is Unknown -> "Biometric status unknown"
    }
}
