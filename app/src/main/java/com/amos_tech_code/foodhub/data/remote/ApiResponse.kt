package com.amos_tech_code.foodhub.data.remote

import retrofit2.Response

sealed class ApiResponse<out T> {

    data class Success<out T>(val data: T) : ApiResponse<T>()

    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>() {
        fun formatMsg(): String {
            return "Error: $code  $message"
        }
    }

    data class Exception(val exception: kotlin.Exception) : ApiResponse<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResponse<T> {

    return try {
        val response = apiCall.invoke()

        if (response.isSuccessful) {
            ApiResponse.Success(response.body()!!)

        } else {
            ApiResponse.Error(response.code(), response.errorBody()?.string() ?: "Unknown Error")
        }

    } catch (e: Exception) {

        ApiResponse.Exception(e)
    }

}