package com.amos_tech_code.foodhub.ui.presentation.feature.auth.signup

import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.data.model.request.SignUpRequest
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import com.amos_tech_code.foodhub.ui.presentation.feature.auth.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    override val foodApi: FoodApi,
    private val session: FoodHubSession
) : BaseAuthViewModel(foodApi) {

    private val _uiState = MutableStateFlow<SignUpEvent>(SignUpEvent.Idle)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onSignUpClicked() {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Loading
            try {
                val response = safeApiCall {
                    foodApi.signUp(
                        SignUpRequest(
                            name = name.value,
                            email = email.value,
                            password = password.value
                        )
                    )
                }
                when (response) {
                    is ApiResponse.Success -> {
                        session.storeToken(response.data.token)
                        _uiState.value = SignUpEvent.Success
                        _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
                    }

                    else -> {
                        val errr = (response as? ApiResponse.Error)?.code ?: 0
                        error = "Sign Up Failed"
                        errorDescription = "Failed to sign up"

                        when (errr) {
                            400 -> {
                                error = "Invalid Credentials"
                                errorDescription = "Please enter correct details."
                            }
                        }
                        _uiState.value = SignUpEvent.Error(error)
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SignUpEvent.Error(e.message?: "Something went wrong")
            }

        }

    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SignUpNavigationEvent.NavigateToLogin)
        }
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Loading
        }
    }

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            errorDescription = msg
            error = "Google Sign In Failed"
            _uiState.value = SignUpEvent.Error(error)
        }
    }

    override fun onFacebookError(msg: String) {
        viewModelScope.launch {
            errorDescription = msg
            error = "Facebook Sign In Failed"
            _uiState.value = SignUpEvent.Error(error)
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value = SignUpEvent.Success
            _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
        }
    }
}


sealed class SignUpNavigationEvent {
    data object NavigateToLogin : SignUpNavigationEvent()

    data object NavigateToHome : SignUpNavigationEvent()
}

sealed class SignUpEvent {
    data object Idle: SignUpEvent()

    data object Loading : SignUpEvent()

    data object Success : SignUpEvent()

    data class Error(val error: String) : SignUpEvent()

}