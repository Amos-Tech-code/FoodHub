package com.amos_tech_code.foodhub.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCartItemRequest(
    val quantity: Int,
    val cartItemId: String
)