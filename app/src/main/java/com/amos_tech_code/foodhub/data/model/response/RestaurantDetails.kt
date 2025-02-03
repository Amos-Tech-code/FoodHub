package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantDetails(
    val address: String,
    val categoryId: String,
    val createdAt: String,
    val distance: String?,
    val id: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val ownerId: String
)