package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class SocketLocationResponse(
    val currentLocation: CurrentLocation,
    val deliveryPhase: String,
    val estimatedTime: Int,
    val finalDestination: FinalDestination,
    val nextStop: NextStop,
    val polyline: String
)

@Serializable
data class CurrentLocation(
    val address: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class FinalDestination(
    val address: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class NextStop(
    val address: String,
    val latitude: Double,
    val longitude: Double
)