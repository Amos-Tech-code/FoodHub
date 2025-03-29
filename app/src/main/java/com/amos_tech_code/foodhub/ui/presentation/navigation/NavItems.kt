package com.amos_tech_code.foodhub.ui.presentation.navigation

import com.amos_tech_code.foodhub.R

sealed class NavItems(val route: NavRoute, val icon: Int) {

    data object Home : NavItems(com.amos_tech_code.foodhub.ui.presentation.navigation.Home, R.drawable.ic_home)

    data object Notifications : NavItems(Notification, R.drawable.ic_notification)

    data object Orders : NavItems(OrderList, R.drawable.ic_delivery)

    data object Cart : NavItems(com.amos_tech_code.foodhub.ui.presentation.navigation.Cart, R.drawable.ic_cart)
}