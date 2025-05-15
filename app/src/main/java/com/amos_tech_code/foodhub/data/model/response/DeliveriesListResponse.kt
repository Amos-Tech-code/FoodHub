package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class DeliveriesListResponse(
    val `data`: List<Deliveries>
)

@Serializable
data class Deliveries(
    val createdAt: String,
    val customerAddress: String,
    val estimatedDistance: Double,
    val estimatedEarning: Double,
    val orderAmount: Double,
    val orderId: String,
    val restaurantAddress: String,
    val restaurantName: String
)
