package com.amos_tech_code.foodhub.data.repository

import com.amos_tech_code.foodhub.data.SocketService
import com.amos_tech_code.foodhub.ui.presentation.feature.orders.LocationUpdateBaseRepository
import javax.inject.Inject

class CustomerLocationUpdateSocketRepository @Inject constructor(
    socketService: SocketService,
) : LocationUpdateBaseRepository(socketService) {

    override fun connectSocket(orderId: String, riderId: String) {
        try {
            socketService.connectSocket(orderId, riderId, null, null)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun disconnectSocket() {
        socketService.disconnectSocket()
    }
}