package com.amos_tech_code.foodhub.data.model.response

import com.amos_tech_code.foodhub.data.model.Address
import kotlinx.serialization.Serializable

@Serializable
data class OrderListResponse(
    val orders: List<Order>
)

@Serializable
data class Order(
    val address: Address,
    val createdAt: String,
    val id: String,
    val items: List<OrderItem>,
    val paymentStatus: String,
    val restaurant: Restaurant,
    val riderId: String? = null,
    val restaurantId: String,
    val status: String,
    val stripePaymentIntentId: String,
    val totalAmount: Double,
    val updatedAt: String,
    val userId: String
)

@Serializable
data class OrderItem(
    val id: String,
    val menuItemId: String,
    val orderId: String,
    val quantity: Int,
    val menuItemName: String? = null,
)