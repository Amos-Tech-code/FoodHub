package com.amos_tech_code.foodhub.ui.presentation.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.amos_tech_code.foodhub.model.UIFoodItem
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

val foodItemNavType = object : NavType<UIFoodItem>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): UIFoodItem? {
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

    override fun parseValue(value: String): UIFoodItem {
        return Json.decodeFromString(UIFoodItem.serializer(), value)
    }

    override fun serializeAsValue(value: UIFoodItem): String {
        return Json.encodeToString(
            UIFoodItem.serializer(), value.copy(
                arModelUrl = URLEncoder.encode(value.arModelUrl, "UTF-8"),
                imageUrl = URLEncoder.encode(value.imageUrl, "UTF-8")
            )
        )
    }

    override fun put(bundle: Bundle, key: String, value: UIFoodItem) {
        return bundle.putString(key, serializeAsValue(value))
    }

}