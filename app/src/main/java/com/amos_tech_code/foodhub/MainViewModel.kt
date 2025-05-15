package com.amos_tech_code.foodhub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    fun navigateToOrderDetail(orderID: String) {
        viewModelScope.launch {
            _event.emit(HomeEvent.NavigateToOrderDetail(orderID))
        }
    }

    sealed class HomeEvent {
        data class NavigateToOrderDetail(val orderID: String) : HomeEvent()
    }
}