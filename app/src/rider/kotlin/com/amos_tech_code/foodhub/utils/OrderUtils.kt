package com.amos_tech_code.foodhub.utils

object OrderUtils {

    enum class OrderStatus {
        ASSIGNED,
        OUT_FOR_DELIVERY, // Rider picked up
        DELIVERED,        // Order completed
        DELIVERY_FAILED,        // Order delivery failed
        REJECTED,         // Restaurant rejected the order
        CANCELLED         // Customer cancelled
    }

}