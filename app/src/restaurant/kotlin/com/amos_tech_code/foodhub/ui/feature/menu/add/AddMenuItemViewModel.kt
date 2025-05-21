package com.amos_tech_code.foodhub.ui.feature.menu.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@HiltViewModel
class AddMenuItemViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val session: FoodHubSession,
    @ApplicationContext val context: Context
) :ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _price = MutableStateFlow("")
    val price = _price.asStateFlow()

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl = _imageUrl.asStateFlow()


    private val _addMenuItemState = MutableStateFlow<AddMenuItemState>(AddMenuItemState.Idle)
    val addMenuItemState = _addMenuItemState.asStateFlow()

    private val _addMenuItemEvent = MutableSharedFlow<AddMenuItemEvent>()
    val addMenuItemEvent = _addMenuItemEvent.asSharedFlow()


    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onDescriptionChange(description: String) {
        _description.value = description
    }

    fun onPriceChange(price: String) {
        _price.value = price
    }

    fun onImageUrlChange(imageUrl: Uri?) {
        _imageUrl.value = imageUrl
    }

    fun addMenuItem() {
        val name = name.value
        val description = description.value
        val price = price.value.toDoubleOrNull() ?: 0.0
        val restaurantId = session.getRestaurantId() ?: ""

        if (name.isEmpty() || description.isEmpty() || price == 0.0 || imageUrl.value == null) {
            _addMenuItemEvent.tryEmit(AddMenuItemEvent.ShowErrorMessage("Please fill all fields"))
            return
        }
        viewModelScope.launch {
            _addMenuItemState.value = AddMenuItemState.Loading
            val imageUrl = uploadImage(imageUri = imageUrl.value!!)
            if (imageUrl == null) {
                _addMenuItemState.value = AddMenuItemState.Error("Failed to upload image. Please try again later.")
                return@launch
            }
            val response = safeApiCall {
                foodApi.addRestaurantMenu(
                    restaurantId,
                    FoodItem(
                        name = name,
                        description = description,
                        price = price,
                        imageUrl = imageUrl,
                        restaurantId = restaurantId
                    )
                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    _addMenuItemState.value = AddMenuItemState.Success("Item added successfully")
                    _addMenuItemEvent.emit(AddMenuItemEvent.GoBack)
                }

                is ApiResponse.Error -> {
                    _addMenuItemState.value = AddMenuItemState.Error(response.message)
                }

                is ApiResponse.Exception -> {
                    _addMenuItemState.value = AddMenuItemState.Error(response.exception.message ?: "An unknown error occurred")
                }
            }
        }
    }

    private suspend fun uploadImage(imageUri: Uri): String? {
        val file = fileFromUri(imageUri)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
        val response = safeApiCall { foodApi.uploadImage(multipartBody) }
        return when (response) {
            is ApiResponse.Success -> {
                response.data.url
            }

            else -> { null }
        }
    }

    private fun fileFromUri(imageUri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val file = File.createTempFile(
            "temp-${System.currentTimeMillis()}-foodhub",
            "jpg",
            context.cacheDir
        )
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun onImageClicked() {
        viewModelScope.launch {
            _addMenuItemEvent.emit(AddMenuItemEvent.AddNewImage)
        }
    }

    sealed class AddMenuItemState {
        data object Idle : AddMenuItemState()
        data object Loading : AddMenuItemState()
        data class Success(val message: String) : AddMenuItemState()
        data class Error(val message: String) : AddMenuItemState()
    }

    sealed class AddMenuItemEvent {
        data class ShowErrorMessage(val message: String) : AddMenuItemEvent()
        data object AddNewImage : AddMenuItemEvent()
        data object GoBack : AddMenuItemEvent()
    }

}