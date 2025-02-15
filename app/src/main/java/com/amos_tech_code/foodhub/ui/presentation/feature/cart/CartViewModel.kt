package com.amos_tech_code.foodhub.ui.presentation.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.request.UpdateCartItemRequest
import com.amos_tech_code.foodhub.data.model.response.CartItem
import com.amos_tech_code.foodhub.data.model.response.CartResponse
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
class CartViewModel @Inject constructor(
    private val foodApi: FoodApi
): ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CartEvent>()
    val event = _event.asSharedFlow()

    private var cartResponse : CartResponse? = null

    var errorTitle : String = ""
    var errorMessage : String = ""

    private var _cartItemsCount = MutableStateFlow(0)
    val itemCount = _cartItemsCount.asStateFlow()

    init {
        getCart()
    }

    fun getCart() {
        viewModelScope.launch {
            val response = safeApiCall {
                foodApi.getCartItems()
            }

            when(response) {
                is ApiResponse.Error -> {
                    _uiState.value = CartUiState.Error(response.message)
                }
                is ApiResponse.Exception -> {
                    _uiState.value = CartUiState.Error(response.exception.message ?: "Something went wrong")
                }
                is ApiResponse.Success -> {
                    cartResponse = response.data
                    _cartItemsCount.value = response.data.items.size
                    _uiState.value = CartUiState.Success(cartResponse!!)
                }
            }
        }
    }

    fun incrementQuantity(cartItem: CartItem) {
        if (cartItem.quantity == 10) {
            return
        }
        updateQuantity(cartItem, cartItem.quantity ++)
    }

    fun decrementQuantity(cartItem: CartItem) {
        if (cartItem.quantity == 1) {
            return
        }
        updateQuantity(cartItem, cartItem.quantity --)
    }

    private fun updateQuantity(cartItem: CartItem, quantity: Int) {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading
            val response = safeApiCall {
                foodApi.updateCart(
                    UpdateCartItemRequest(
                        quantity = cartItem.quantity,
                        cartItemId = cartItem.id
                    )
                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    getCart()
                }
                 else -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                     errorTitle = "Error Updating Cart"
                     errorMessage = "An error occurred when trying to update cart."
                    _event.emit(CartEvent.OnQuantityUpdateError)
                }

            }
        }
    }

    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading

            val response = safeApiCall {
                foodApi.deleteCartItem(cartItem.id)
            }

            when (response) {
                is ApiResponse.Success -> {
                    getCart()
                }
                else -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                    errorTitle = "Error"
                    errorMessage = "An error occurred when trying to delete Item."
                    _event.emit(CartEvent.OnRemoveItemError)
                }

            }
        }
    }

    fun retry() {
        getCart()
    }

    fun onAddressClicked() {
        viewModelScope.launch {
            _event.emit(CartEvent.OnAddressClicked)
        }
    }

    fun checkOut() {

    }

    sealed class CartUiState {
        data object Loading : CartUiState()

        data class Success(val data: CartResponse) : CartUiState()

        data class Error(val message: String) : CartUiState()

        data object Nothing : CartUiState()
    }

    sealed class CartEvent {
        data object OnCheckOut : CartEvent()

        data object OnAddressClicked : CartEvent()

        data object ShowErrorDialog : CartEvent()

        data object OnRemoveItemError : CartEvent()

        data object OnQuantityUpdateError : CartEvent()
    }

}