package com.amos_tech_code.foodhub.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val createdAt: String,
    val id: String,
    val imageUrl: String,
    val name: String
)