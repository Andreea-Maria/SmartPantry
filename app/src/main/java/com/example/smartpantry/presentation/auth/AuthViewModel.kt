package com.example.smartpantry.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpantry.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(authRepository.isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)

            if (result.isSuccess) {
                _isLoggedIn.value = true
                _errorMessage.value = null
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.register(email,password)

            if (result.isSuccess) {
                _isLoggedIn.value = true
                _errorMessage.value = null
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _isLoggedIn.value = false
    }
}