package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantDetailsResponse(
    val `data`: RestaurantDetails
)