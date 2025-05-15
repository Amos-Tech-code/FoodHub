package com.amos_tech_code.foodhub.ui.presentation.feature.orders

import com.amos_tech_code.foodhub.data.SocketService

abstract class LocationUpdateBaseRepository (val socketService: SocketService) {

    open val messages = socketService.messages

    abstract fun connectSocket(
        orderId: String,
        riderId: String,
    )

    abstract fun disconnectSocket()

}