package com.amos_tech_code.foodhub.data

import kotlinx.coroutines.flow.Flow

interface SocketService {

    fun connectSocket(
        orderId: String,
        riderId: String,
        lat: Double?,
        lng: Double?
    )

    fun disconnectSocket()

    fun sendMessage(message: String)

    val messages : Flow<String>

}