package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val arModelUrl: String?,
    val createdAt: String,
    val description: String,
    val id: String,
    val imageUrl: String,
    val name: String,
    val price: Double,
    val restaurantId: String
)