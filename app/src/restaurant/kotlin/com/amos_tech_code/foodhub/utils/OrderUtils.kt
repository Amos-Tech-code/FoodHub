package com.amos_tech_code.foodhub.utils

object OrderUtils {

    enum class OrderStatus {
        PENDING_ACCEPTANCE, // Initial state when order is placed
        ACCEPTED,          // Restaurant accepted the order
        PREPARING,         // Food is being prepared
        READY,            // Ready for delivery/pickup
        DELIVERED,        // Order completed
    }

}