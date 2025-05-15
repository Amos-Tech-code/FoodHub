package com.amos_tech_code.foodhub.ui.presentation.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.response.Notification
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationUIState>(NotificationUIState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<NotificationEvent>()
    val event = _event.asSharedFlow()

    private val _unreadNotificationCount = MutableStateFlow(0)
    val unreadNotificationCount = _unreadNotificationCount.asStateFlow()

    init {
        getNotifications()
    }

    private fun navigateToOrderDetails(orderID: String) {
        viewModelScope.launch {
            _event.emit(NotificationEvent.NavigateToOrderDetails(orderID))
        }
    }

    fun readNotifications(notification: Notification) {
        viewModelScope.launch {
            navigateToOrderDetails(notification.orderId)
            val result = safeApiCall {
                foodApi.readNotification(notification.id)
            }
            if (result is ApiResponse.Success) {
                getNotifications()
            }
        }
    }

    fun getNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationUIState.Loading
            try {
                val result = safeApiCall {
                    foodApi.getNotifications()
                }
                when (result) {
                    is ApiResponse.Success -> {
                        _unreadNotificationCount.value = result.data.unreadCount
                        _uiState.value = NotificationUIState.Success(result.data.notifications)
                    }
                    is ApiResponse.Error -> {
                        _uiState.value = NotificationUIState.Error(result.message)
                    }
                    is ApiResponse.Exception -> {
                        _uiState.value = NotificationUIState.Error(
                            result.exception.message ?: "Unknown error"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = NotificationUIState.Error(e.message ?: "Unknown error")
            }
        }

    }




    sealed class NotificationEvent {
        data class NavigateToOrderDetails(val orderID: String) : NotificationEvent()

    }

    sealed class NotificationUIState {
        data object Loading : NotificationUIState()

        data class Error(val message: String) : NotificationUIState()

        data class Success(val data: List<Notification>) : NotificationUIState()

    }

}