package com.amos_tech_code.foodhub.ui.presentation.feature.auth.login

import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.data.model.request.SignInRequest
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
class SignInViewModel @Inject constructor(
    override val foodApi: FoodApi,
    private val session: FoodHubSession
) : BaseAuthViewModel(foodApi) {

    private val _uiState = MutableStateFlow<SignInEvent>(SignInEvent.Idle)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignInNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignInClicked() {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
            val response = safeApiCall {
                foodApi.signIn(
                    SignInRequest(
                        email = email.value,
                        password = password.value
                    )
                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = SignInEvent.Success
                    session.storeToken(response.data.token)
                    _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
                }
                is ApiResponse.Error -> {
                    error = "Sign In Failed"
                    errorDescription = response.message
                    _uiState.value = SignInEvent.Error(response.message)
                }
                is ApiResponse.Exception -> {
                    error = "Sign In Failed"
                    errorDescription = response.exception.message ?: "Unknown Error"
                    _uiState.value = SignInEvent.Error(response.exception.message ?: "Unknown Error")
                }

            }
        }

    }

    fun onSignUpClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SignInNavigationEvent.NavigateToSignUp)
        }
    }

    sealed class SignInNavigationEvent {
        data object NavigateToSignUp : SignInNavigationEvent()

        data object NavigateToHome : SignInNavigationEvent()
    }

    sealed class SignInEvent {
        data object Idle: SignInEvent()

        data object Loading : SignInEvent()

        data object Success : SignInEvent()

        data class Error(val error: String) : SignInEvent()

    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
        }
    }

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            errorDescription = msg
            error = "Google Sign In Failed"
            _uiState.value = SignInEvent.Error(error)
        }
    }

    override fun onFacebookError(msg: String) {
        viewModelScope.launch {
            errorDescription = msg
            error = "Facebook Sign In Failed"
            _uiState.value = SignInEvent.Error(error)
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value = SignInEvent.Success
            _navigationEvent.emit(SignInNavigationEvent.NavigateToHome)
        }
    }

}