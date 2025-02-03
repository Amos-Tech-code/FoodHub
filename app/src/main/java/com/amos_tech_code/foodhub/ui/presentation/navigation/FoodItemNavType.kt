package com.amos_tech_code.foodhub.ui.presentation.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

val foodItemNavType = object : NavType<FoodItem>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): FoodItem? {
        return parseValue(bundle.getString(key).toString()).copy(
            arModelUrl = URLDecoder.decode(
                parseValue(bundle.getString(key).toString()).arModelUrl,
                "UTF-8"
            ),
            imageUrl = URLDecoder.decode(
                parseValue(bundle.getString(key).toString()).imageUrl,
                "UTF-8"
            )
        )
    }

    override fun parseValue(value: String): FoodItem {
        return Json.decodeFromString(FoodItem.serializer(), value)
    }

    override fun serializeAsValue(value: FoodItem): String {
        return Json.encodeToString(
            FoodItem.serializer(), value.copy(
                arModelUrl = URLEncoder.encode(value.arModelUrl, "UTF-8"),
                imageUrl = URLEncoder.encode(value.imageUrl, "UTF-8")
            )
        )
    }

    override fun put(bundle: Bundle, key: String, value: FoodItem) {
        return bundle.putString(key, serializeAsValue(value))
    }

}