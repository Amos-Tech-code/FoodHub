package com.amos_tech_code.foodhub.data.remote

import kotlinx.coroutines.delay
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ApiResponse<out T> {

    data class Success<out T>(val data: T) : ApiResponse<T>()

    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>() {
        fun formatMsg(): String {
            return if (code == 0) {
                message
            } else {
                "Error: $code $message"
            }
        }
    }

    data class Exception(val exception: kotlin.Exception) : ApiResponse<Nothing>()
}



suspend fun <T> safeApiCall(
    retryCount: Int = 1,
    retryDelayMillis: Long = 500L,
    apiCall: suspend () -> Response<T>,
): ApiResponse<T> {

    var currentAttempt = 0

    // Check for internet connectivity before making the API call
    if (!NetworkMonitorProvider.get().isConnected()) {
        return ApiResponse.Error(0, "No internet connection. Please check your network.")
    }

    while (currentAttempt <= retryCount) {
        try {
            val response = apiCall.invoke()
            if (response.isSuccessful && response.body() != null) {
                return ApiResponse.Success(response.body()!!)
            } else {

                val errorMessage = when (response.code()) {
                    400 -> "Bad Request. Please check your input."
                    401 -> "Unauthorized. Please log in again."
                    402 -> "Payment Required."
                    403 -> "Access Forbidden."
                    404 -> "Resource not found."
                    500 -> "Internal server error. Please try again later."
                    502 -> "Bad Gateway."
                    503 -> "Service is currently Unavailable."
                    504 -> "Gateway Timeout."
                    else -> {
                        "Unexpected server error (Code ${response.code()}). Please try again later."
                    }
                }
                return ApiResponse.Error(
                    code = response.code(),
                    message = errorMessage
                )
            }
        } catch (e: Exception) {

            when (e) {
                is UnknownHostException, is SocketTimeoutException, is ConnectException -> {
                    if (currentAttempt < retryCount) {
                        delay(retryDelayMillis)
                        currentAttempt++
                        continue
                    } else {
                        return ApiResponse.Error(0, when (e) {
                            is UnknownHostException -> "No Internet Connection. Please check your network."
                            is SocketTimeoutException -> "Connection timed out. Please try again later."
                            is ConnectException -> "Unable to connect to the server. Please try again."
                            else -> "Temporary network error. Please try again later."
                        })
                    }
                }
                is IOException -> {
                    return ApiResponse.Error(0, "Network Error. Check your network and try again.")
                }
                else -> {
                    return ApiResponse.Exception(e)
                }
            }
        }
    }

    return ApiResponse.Error(0, "Unknown error occurred.")
}
