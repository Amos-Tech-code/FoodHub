package com.amos_tech_code.foodhub.ui.presentation.navigation

import com.amos_tech_code.foodhub.R

sealed class NavItems(val route: NavRoute, val icon: Int) {

    data object Home : NavItems(com.amos_tech_code.foodhub.ui.presentation.navigation.Home, R.drawable.ic_home)

    data object Notifications : NavItems(Notification, R.drawable.ic_notification)

    data object Orders : NavItems(OrderList, R.drawable.ic_delivery)

    data object Favourites : NavItems(Favourite, R.drawable.ic_heart)

    data object Cart : NavItems(com.amos_tech_code.foodhub.ui.presentation.navigation.Cart, R.drawable.ic_cart)

    data object Menu : NavItems(ListMenuItems, android.R.drawable.ic_menu_more)
}

sealed class NavigationDrawerItems(
    val route: NavRoute,
    val icon: Int,
    val label: String
) {
    data object MyOrders : NavigationDrawerItems(
        OrderList,
        R.drawable.ic_order_list,
        "My Orders"

    )

    data object MyProfile : NavigationDrawerItems(
        Home,
        R.drawable.ic_profile,
        "My Profile"
    )

    data object DeliveryAddress : NavigationDrawerItems(
        AddressList,
        R.drawable.ic_location,
        "Delivery Address"
    )

    data object PaymentMethod : NavigationDrawerItems(
        Home,
        R.drawable.ic_wallet,
        "Payment Method"

    )

    data object ContactUs : NavigationDrawerItems(
        Home,
        R.drawable.ic_contact,
        "Contact Us"
    )

    data object Settings : NavigationDrawerItems(
        Home,
        R.drawable.ic_setting,
        "Settings"
    )

    data object HelpAndSupport : NavigationDrawerItems(
        Home,
        R.drawable.ic_help,
        "Help & Support"
    )

}