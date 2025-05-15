package com.amos_tech_code.foodhub.ui.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodHubSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val foodHubSession: FoodHubSession
): ViewModel() {

    private val _navigationEvent = MutableSharedFlow<ProfileNavigationEvents?>()
    val navigationEvents = _navigationEvent.asSharedFlow()

    fun logOut() {
        viewModelScope.launch {
            foodHubSession.clearSession()
            _navigationEvent.emit(ProfileNavigationEvents.NavigateToLogin)
        }
    }


    sealed class ProfileNavigationEvents {
        data object NavigateToLogin : ProfileNavigationEvents()

    }
}