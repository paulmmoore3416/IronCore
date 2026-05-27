package com.ironcore.metrics.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signIn() {
        // Mock sign in process
        _authState.value = AuthState.LoggedIn("Iron User", "user@ironcore.com")
    }

    fun signOut() {
        _authState.value = AuthState.LoggedOut
    }
}

sealed class AuthState {
    object LoggedOut : AuthState()
    data class LoggedIn(val name: String, val email: String) : AuthState()
}
