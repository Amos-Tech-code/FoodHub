package com.amos_tech_code.foodhub.utils

object OrderUtils {

    enum class OrderStatus {
        PENDING_ACCEPTANCE, // Initial state when order is placed
        ACCEPTED,          // Restaurant accepted the order
        PREPARING,         // Food is being prepared
        READY,            // Ready for delivery/pickup
        ASSIGNED,
        OUT_FOR_DELIVERY, // Rider picked up
        DELIVERED,        // Order completed
        DELIVERY_FAILED,   // Order delivery failed
        REJECTED,         // Restaurant rejected the order
        CANCELLED         // Customer cancelled
    }

}