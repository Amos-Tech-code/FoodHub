package com.amos_tech_code.foodhub.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PaymentIntentRequest(
    val addressId: String
)
