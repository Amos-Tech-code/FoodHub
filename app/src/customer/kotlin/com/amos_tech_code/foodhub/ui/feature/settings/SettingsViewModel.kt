package com.amos_tech_code.foodhub.ui.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.ui.feature.home.HomeViewModel.HomeScreenNavigationEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val foodHubSession: FoodHubSession
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsScreenState>(SettingsScreenState.Idle)
    val uiState: StateFlow<SettingsScreenState> = _uiState

    private val _event = MutableSharedFlow<SettingsScreenEvents>()
    val events: SharedFlow<SettingsScreenEvents> = _event

    private val _darkThemeEnabled = MutableStateFlow(false)
    val darkThemeEnabled: StateFlow<Boolean> = _darkThemeEnabled

    fun logOut() {
        viewModelScope.launch {
            foodHubSession.clearSession()
            _event.emit(SettingsScreenEvents.NavigateToLogin)
        }
    }


    sealed class SettingsScreenState {
        data object Idle : SettingsScreenState()

        data object Loading : SettingsScreenState()

        data object Error : SettingsScreenState()

        data object Success : SettingsScreenState()

    }

    sealed class SettingsScreenEvents {
        data object NavigateToLogin : SettingsScreenEvents()

    }

}