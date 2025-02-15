package com.amos_tech_code.foodhub.utils

object StringUtils {

    fun formatCurrency(value: Double): String {
        val formatter = java.text.NumberFormat.getCurrencyInstance()
        formatter.currency = java.util.Currency.getInstance("USD")
        return formatter.format(value)
    }
}