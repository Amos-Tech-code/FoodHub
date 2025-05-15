package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmPaymentResponse(
    val status: String,
    val requiresAction: Boolean,
    val clientSecret: String,
    val orderId: String?,
    val orderStatus: String?,
    val message: String?
)
