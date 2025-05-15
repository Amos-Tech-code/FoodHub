package com.amos_tech_code.foodhub.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocodeRequest(
    val latitude: Double,
    val longitude: Double
)
