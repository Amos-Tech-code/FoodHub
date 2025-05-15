package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PaymentIntentResponse(
    val paymentIntentClientSecret: String,
    val paymentIntentId: String,
    val customerId: String,
    val ephemeralKeySecret: String,
    val publishableKey: String,
    val amount: Int,
    val currency: String,
    val status: String
)
