package com.amos_tech_code.foodhub.data.model.response

data class RiderDeliveryOrderListResponse(
    val `data`: List<DeliveryOrder>
)


data class DeliveryOrder(
    val createdAt: String,
    val customer: Customer,
    val estimatedEarning: Double,
    val items: List<DeliveryOrderItem>,
    val orderId: String,
    val restaurant: Restaurant,
    val status: String,
    val totalAmount: Double,
    val updatedAt: String
)


data class DeliveryOrderItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int
)


data class Customer(
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val state: String,
    val zipCode: String
)