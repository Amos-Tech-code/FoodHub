package com.amos_tech_code.foodhub.data

import android.content.Context
import android.content.SharedPreferences

class FoodHubSession(private val context: Context) {

    private val sharedPreferences : SharedPreferences =
        context.getSharedPreferences("food_hub", Context.MODE_PRIVATE)

    fun storeToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)  // Directly return the value
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()

    }
}