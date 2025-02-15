package com.amos_tech_code.foodhub.ui.presentation.feature.address.add_address

import androidx.lifecycle.ViewModel
import com.amos_tech_code.foodhub.data.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAddressUiState>(AddAddressUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddAddressEvent>()
    val event = _event.asSharedFlow()


    fun reverseGeocode(lat: Double, lng: Double) {

    }

    fun addAddress() {

    }

    sealed class AddAddressUiState {
        data object Loading : AddAddressUiState()

        data class Success(val data: String) : AddAddressUiState()

        data class Error(val message: String) : AddAddressUiState()
    }

    sealed class AddAddressEvent {
        data object NavigateToAddressList : AddAddressEvent()

    }

}