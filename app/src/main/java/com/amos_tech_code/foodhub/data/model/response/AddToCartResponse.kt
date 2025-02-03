package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AddToCartResponse(
    val id: String,
    val message: String
)
