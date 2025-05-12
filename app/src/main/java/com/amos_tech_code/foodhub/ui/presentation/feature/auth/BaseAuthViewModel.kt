package com.amos_tech_code.foodhub.ui.presentation.feature.auth

import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.auth.GoogleAuthUiProvider
import com.amos_tech_code.foodhub.data.model.request.OauthRequest
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(open val foodApi: FoodApi) : ViewModel() {

        var error: String = ""
        var errorDescription = ""
        private val googleAuthUiProvider = GoogleAuthUiProvider()
        private lateinit var callbackManager: CallbackManager

        abstract fun loading()
        abstract fun onGoogleError(msg: String)
        abstract fun onFacebookError(msg: String)
        abstract fun onSocialLoginSuccess(token: String)

        fun onFacebookClicked(context: ComponentActivity) {
            initiateFacebookLogin(context)
        }

        fun onGoogleClicked(context: ComponentActivity) {
            initiateGoogleLogin(context)
        }

        private fun initiateGoogleLogin(context: ComponentActivity) {
            viewModelScope.launch {
                loading()
                try {
                    val response = googleAuthUiProvider.signIn(
                        context, CredentialManager.create(context)
                    )
                    fetchFoodAppToken(response.token, "google") {
                        onGoogleError(it)
                    }
                } catch (e: Throwable) {
                    onGoogleError(e.message.toString())
                }
            }
        }


        private fun initiateFacebookLogin(context: ComponentActivity) {
            loading()
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        fetchFoodAppToken(result.accessToken.token, "facebook") {
                            onFacebookError(it)
                        }
                    }

                    override fun onCancel() {
                        onFacebookError("Cancelled")
                    }

                    override fun onError(error: FacebookException) {
                        onFacebookError("Failed: ${error.message}")
                    }
                })

            LoginManager.getInstance().logInWithReadPermissions(
                context,
                callbackManager,
                listOf("public_profile", "email"),
            )
        }


        private fun fetchFoodAppToken(token: String, provider: String, onError: (String) -> Unit) {
            viewModelScope.launch {
                val request = OauthRequest(
                    token = token,
                    provider = provider
                )
                val res = safeApiCall { foodApi.socialSignIn(request) }

                when (res) {
                    is ApiResponse.Success -> {
                        onSocialLoginSuccess(res.data.token)
                    }
                    is ApiResponse.Error -> {
                        onError(res.message)
                    }
                    is ApiResponse.Exception -> {
                        onError(res.exception.message ?: "Unknown Error")
                    }
                }
            }
        }

}