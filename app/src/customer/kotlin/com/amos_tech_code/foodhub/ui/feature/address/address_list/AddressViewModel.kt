package com.amos_tech_code.foodhub.ui.feature.address.address_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.Address
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
class AddressViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddressUiState>(AddressUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddressEvent>()
    val event = _event.asSharedFlow()

    init {
        getAddress()
    }

    fun getAddress() {
        viewModelScope.launch {
            _uiState.value = AddressUiState.Loading
            try {
                val result = safeApiCall {
                    foodApi.getAddress()
                }
                when (result) {
                    is ApiResponse.Success -> {
                        _uiState.value = AddressUiState.Success(result.data.addresses)

                    }
                    is ApiResponse.Error -> {
                        _uiState.value = AddressUiState.Error(result.message)
                    }
                    else -> {
                        _uiState.value = AddressUiState.Error("Something went wrong")
                    }
                }

            } catch (e: Exception) {
                _uiState.value = AddressUiState.Error(e.message ?: "Something went wrong")
            }

        }
    }

    fun onAddAddressClicked() {
        viewModelScope.launch {
            _event.emit(AddressEvent.NavigateToAddAddress)
        }
    }

    fun onAddressSelected(address: Address) {
        viewModelScope.launch {
            _event.emit(AddressEvent.NavigateBack(address))
        }
    }

    sealed class AddressUiState {
        data object Loading : AddressUiState()

        data class Success(val data: List<Address>) : AddressUiState()

        data class Error(val message: String) : AddressUiState()

    }

    sealed class AddressEvent {
        data class NavigateToEditAddress(val address: Address) : AddressEvent()

        data object ShowErrorDialog : AddressEvent()

        data object NavigateToAddAddress : AddressEvent()

        data class NavigateBack(val address: Address) : AddressEvent()
    }
}