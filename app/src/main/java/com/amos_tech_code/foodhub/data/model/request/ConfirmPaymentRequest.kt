package com.amos_tech_code.foodhub.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmPaymentRequest(
    val paymentIntentId: String,
    val addressId: String,
)
