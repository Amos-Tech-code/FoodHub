package com.amos_tech_code.foodhub.ui.feature.address.add_address

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.Address
import com.amos_tech_code.foodhub.data.model.request.ReverseGeocodeRequest
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import com.amos_tech_code.foodhub.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAddressUiState>(AddAddressUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddAddressEvent>()
    val event = _event.asSharedFlow()

    private val _address = MutableStateFlow<Address?>(null)
    val address = _address.asStateFlow()

    fun getLocation(): Flow<Location> = locationManager.getLocation()
        .catch { e ->
            _uiState.value = AddAddressUiState.Error("Location error: ${e.message}")
            emit(Location("").apply {
                latitude = -1.2921  // Nairobi latitude
                longitude = 36.8219  // Nairobi longitude
            })
        }

    fun reverseGeocode(lat: Double, lng: Double) {
        viewModelScope.launch {
            _address.value = null
            val address = safeApiCall {
                foodApi.reverseGeocode(ReverseGeocodeRequest(lat, lng))
            }

            when (address) {
                is ApiResponse.Success -> {
                    _address.value = address.data
                    _uiState.value = AddAddressUiState.Success
                }
                else -> {
                    _address.value = null
                    _uiState.value = AddAddressUiState.Error("Failed to reverse geocode")
                    _event.emit(AddAddressEvent.ShowToast("Failed to reverse geocode"))
                }
            }
        }

    }

    fun onAddAddressClicked() {
        viewModelScope.launch {
            _uiState.value = AddAddressUiState.AddressStoring
            val result = safeApiCall {
                foodApi.storeAddress(_address.value!!)

            }
            when (result) {
                is ApiResponse.Success -> {
                    _uiState.value = AddAddressUiState.Success
                    _event.emit(AddAddressEvent.NavigateToAddressList)
                }
                else -> {
                    _uiState.value = AddAddressUiState.Error("Failed to store address")
                    _event.emit(AddAddressEvent.ShowToast("Failed to store address"))

                }

            }
        }

    }

    sealed class AddAddressUiState {
        data object Loading : AddAddressUiState()

        data object Success : AddAddressUiState()

        data object AddressStoring : AddAddressUiState()

        data class Error(val message: String) : AddAddressUiState()
    }

    sealed class AddAddressEvent {
        data object NavigateToAddressList : AddAddressEvent()

        data class ShowToast(val message: String) : AddAddressEvent()

    }

}