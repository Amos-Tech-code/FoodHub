package com.amos_tech_code.foodhub.data.model.response

import com.amos_tech_code.foodhub.data.model.Address
import kotlinx.serialization.Serializable

@Serializable
data class AddressResponse(
    val addresses: List<Address>
)
