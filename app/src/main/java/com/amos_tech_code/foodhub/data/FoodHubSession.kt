package com.amos_tech_code.foodhub.data

import android.content.Context
import android.content.SharedPreferences

class FoodHubSession(private val context: Context) {

    private val sharedPreferences : SharedPreferences =
        context.getSharedPreferences("food_hub", Context.MODE_PRIVATE)

    fun storeToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken() : String? {
        sharedPreferences.getString("token", null)?.let {
            return it
        }
        return null
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()

    }
}