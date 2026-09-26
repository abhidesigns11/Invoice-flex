package com.example.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthRepository
import com.example.data.model.UserProfile
import com.example.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Where the app should currently be showing the person. */
enum class SessionStatus {
    CHECKING,       // still figuring out if someone is logged in
    LOGGED_OUT,     // show Login / Sign up
    NEEDS_SETUP,    // logged in, but the Q&A profile setup isn't done yet
    READY           // logged in and set up — show the real app
}

data class AuthUiState(
    val sessionStatus: SessionStatus = SessionStatus.CHECKING,
    val uid: String? = null,
    val userEmail: String? = null,
    val profile: UserProfile? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val profileRepository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.authStateFlow().collect { user ->
                if (user == null) {
                    _uiState.update {
                        it.copy(sessionStatus = SessionStatus.LOGGED_OUT, uid = null, userEmail = null, profile = null)
                    }
                } else {
                    _uiState.update { it.copy(uid = user.uid, userEmail = user.email) }
                    refreshProfileStatus(user.uid)
                }
            }
        }
    }

    private suspend fun refreshProfileStatus(uid: String) {
        val profile = profileRepository.getProfile(uid)
        _uiState.update {
            it.copy(
                profile = profile,
                sessionStatus = if (profile?.onboardingComplete == true) SessionStatus.READY else SessionStatus.NEEDS_SETUP
            )
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter both email and password.") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            result.onSuccess {
                _uiState.update { it.copy(isSubmitting = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = friendlyError(e.message)) }
            }
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter both email and password.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
            return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.signUp(email, password)
            result.onSuccess {
                _uiState.update { it.copy(isSubmitting = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = friendlyError(e.message)) }
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter your email above first, then tap Forgot password.") }
            return
        }
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)
            result.onSuccess {
                _uiState.update { it.copy(infoMessage = "Password reset email sent to $email") }
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = friendlyError(e.message)) }
            }
        }
    }

    fun logout() {
        authRepository.signOut()
    }

    /**
     * Uploads the logo (if a new one was picked) and saves the completed setup
     * profile to Firestore under the signed-in user's own uid.
     */
    fun completeOnboarding(profileDraft: UserProfile, logoUri: Uri?) {
        val uid = _uiState.value.uid
        if (uid == null) {
            _uiState.update { it.copy(errorMessage = "You're signed out — please log in again.") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            var logoUrl = profileDraft.logoUrl
            if (logoUri != null) {
                val uploadResult = profileRepository.uploadLogo(uid, logoUri)
                uploadResult.onFailure { e ->
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = "Logo upload failed: ${e.message}") }
                    return@launch
                }
                uploadResult.onSuccess { logoUrl = it }
            }

            val finalProfile = profileDraft.copy(uid = uid, logoUrl = logoUrl, onboardingComplete = true)
            val saveResult = profileRepository.saveProfile(finalProfile)
            saveResult.onSuccess {
                _uiState.update {
                    it.copy(isSubmitting = false, profile = finalProfile, sessionStatus = SessionStatus.READY)
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = e.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    private fun friendlyError(raw: String?): String {
        val msg = raw ?: return "Something went wrong. Please try again."
        return when {
            msg.contains("badly formatted", ignoreCase = true) -> "That email address doesn't look right."
            msg.contains("no user record", ignoreCase = true) -> "No account found with that email."
            msg.contains("password is invalid", ignoreCase = true) -> "Incorrect password."
            msg.contains("already in use", ignoreCase = true) -> "An account already exists with that email."
            msg.contains("network", ignoreCase = true) -> "Network error. Check your internet connection."
            else -> msg
        }
    }
}
