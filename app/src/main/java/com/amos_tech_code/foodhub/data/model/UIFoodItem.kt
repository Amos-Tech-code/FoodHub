package com.amos_tech_code.foodhub.data.model

import android.os.Parcelable
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class UIFoodItem(
    val arModelUrl: String,
    val createdAt: String,
    val description: String,
    val id: String,
    val imageUrl: String,
    val name: String,
    val price: Double,
    val restaurantId: String
) : Parcelable {
    companion object {
        fun fromFoodItem(foodItem: FoodItem): UIFoodItem {
            return UIFoodItem(
                arModelUrl = foodItem.arModelUrl ?: "",
                createdAt = foodItem.createdAt ?: "",
                description = foodItem.description,
                id = foodItem.id ?: "",
                imageUrl = foodItem.imageUrl,
                name = foodItem.name,
                price = foodItem.price,
                restaurantId = foodItem.restaurantId
            )
        }
    }
}
