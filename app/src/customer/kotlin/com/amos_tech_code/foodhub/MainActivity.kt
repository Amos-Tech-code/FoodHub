package com.amos_tech_code.foodhub

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.data.model.UIFoodItem
import com.amos_tech_code.foodhub.ui.feature.address.add_address.AddAddressScreen
import com.amos_tech_code.foodhub.ui.feature.address.address_list.AddressScreen
import com.amos_tech_code.foodhub.ui.feature.cart.CartScreen
import com.amos_tech_code.foodhub.ui.feature.cart.CartViewModel
import com.amos_tech_code.foodhub.ui.feature.contactus.ContactUsScreen
import com.amos_tech_code.foodhub.ui.feature.favourites.FavouritesScreen
import com.amos_tech_code.foodhub.ui.feature.food_details.FoodDetailsScreen
import com.amos_tech_code.foodhub.ui.feature.help_faqs.HelpSupportScreen
import com.amos_tech_code.foodhub.ui.feature.home.HomeScreen
import com.amos_tech_code.foodhub.ui.feature.order_details.OrderDetailsScreen
import com.amos_tech_code.foodhub.ui.feature.order_success.OrderSuccessScreen
import com.amos_tech_code.foodhub.ui.feature.orders.OrderListScreen
import com.amos_tech_code.foodhub.ui.feature.payment_method.PaymentMethodsScreen
import com.amos_tech_code.foodhub.ui.feature.profile.ProfileScreen
import com.amos_tech_code.foodhub.ui.feature.restaurants_details.RestaurantDetailsScreen
import com.amos_tech_code.foodhub.ui.feature.settings.SettingsScreen
import com.amos_tech_code.foodhub.ui.presentation.FoodHubNavHost
import com.amos_tech_code.foodhub.ui.presentation.feature.auth.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.auth.login.LoginScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.auth.signup.SignUpScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.NotificationsScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.NotificationsViewModel
import com.amos_tech_code.foodhub.ui.presentation.navigation.AddAddress
import com.amos_tech_code.foodhub.ui.presentation.navigation.AddressList
import com.amos_tech_code.foodhub.ui.presentation.navigation.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.Cart
import com.amos_tech_code.foodhub.ui.presentation.navigation.ContactUs
import com.amos_tech_code.foodhub.ui.presentation.navigation.Favourite
import com.amos_tech_code.foodhub.ui.presentation.navigation.FoodDetails
import com.amos_tech_code.foodhub.ui.presentation.navigation.HelpAndSupport
import com.amos_tech_code.foodhub.ui.presentation.navigation.Home
import com.amos_tech_code.foodhub.ui.presentation.navigation.Login
import com.amos_tech_code.foodhub.ui.presentation.navigation.NavItems
import com.amos_tech_code.foodhub.ui.presentation.navigation.Notification
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderDetails
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderList
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderSuccess
import com.amos_tech_code.foodhub.ui.presentation.navigation.PaymentMethod
import com.amos_tech_code.foodhub.ui.presentation.navigation.Profile
import com.amos_tech_code.foodhub.ui.presentation.navigation.RestaurantsDetails
import com.amos_tech_code.foodhub.ui.presentation.navigation.Setting
import com.amos_tech_code.foodhub.ui.presentation.navigation.SignUp
import com.amos_tech_code.foodhub.ui.presentation.navigation.foodItemNavType
import com.amos_tech_code.foodhub.ui.theme.BrightYellow
import com.amos_tech_code.foodhub.ui.theme.FoodHubTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : BaseFoodHubActivity() {

    private var showSplashScreen = true
    @Inject
    lateinit var session: FoodHubSession

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                showSplashScreen
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.5f,
                    0.0f
                )

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.5f,
                    0f
                )
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomY.doOnEnd {
                    screen.remove()
                }
                zoomX.start()
                zoomY.start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodHubTheme(darkTheme = false) {
                SetStatusBarLight(true)

                val shouldShowBottomNav = remember { mutableStateOf(false) }
                val navItems = listOf(
                    NavItems.Home,
                    NavItems.Cart,
                    NavItems.Orders,
                    NavItems.Favourites,
                    NavItems.Notifications
                )
                val navController = rememberNavController()
                val cartViewModel : CartViewModel = hiltViewModel()
                val cartItemCount = cartViewModel.itemCount.collectAsStateWithLifecycle()
                val notificationsViewModel : NotificationsViewModel = hiltViewModel()
                val unreadNotificationCount = notificationsViewModel.unreadNotificationCount.collectAsStateWithLifecycle()

                LaunchedEffect(key1 = true) {
                    viewModel.event.collectLatest {
                        when(it) {
                            is MainViewModel.HomeEvent.NavigateToOrderDetail -> {
                                navController.navigate(OrderDetails(it.orderID))
                            }
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination

                        AnimatedVisibility(visible = shouldShowBottomNav.value) {
                            NavigationBar(
                                containerColor = Color.White
                            ) {
                                navItems.forEach { item->
                                    val selected =
                                        currentRoute?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                navController.graph.startDestinationRoute?.let { startRoute ->
                                                    popUpTo(startRoute){
                                                        saveState = true
                                                    }
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Box(modifier = Modifier.size(48.dp)) {
                                                Icon(
                                                    painter = painterResource(item.icon),
                                                    contentDescription = null,
                                                    tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .align(Alignment.Center)
                                                )
                                                if(item.route == Cart && cartItemCount.value > 0 ) {
                                                    ItemCount(cartItemCount.value)
                                                }
                                                if(item.route == Notification && unreadNotificationCount.value > 0) {
                                                    ItemCount(unreadNotificationCount.value)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                ) { @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

                    val startDestination = if (session.getToken() != null) Home else AuthScreen

                    SharedTransitionLayout {
                        FoodHubNavHost(
                            navController = navController,
                            startDestination = startDestination,
                        ) {
                            composable<SignUp> {
                                shouldShowBottomNav.value = false
                                SignUpScreen(navController)
                            }

                            composable<Login> {
                                shouldShowBottomNav.value = false
                                LoginScreen(navController)
                            }

                            composable<AuthScreen> {
                                shouldShowBottomNav.value = false
                                AuthScreen(navController)
                            }

                            composable<Home> {
                                shouldShowBottomNav.value = true
                                HomeScreen(navController, this)
                            }

                            composable<RestaurantsDetails> {
                                shouldShowBottomNav.value = false
                                val route = it.toRoute<RestaurantsDetails>()
                                RestaurantDetailsScreen(
                                    navController = navController,
                                    name = route.name,
                                    imgUrl = route.imgUrl,
                                    restaurantId = route.restaurantId,
                                    this
                                )
                            }

                            composable<FoodDetails>(
                                typeMap = mapOf(typeOf<UIFoodItem>() to foodItemNavType)
                            ) {
                                shouldShowBottomNav.value = false
                                val route = it.toRoute<FoodDetails>()
                                FoodDetailsScreen(
                                    navController = navController,
                                    foodItem = route.foodItem,
                                    onAddToCartClicked = { cartViewModel.getCart() },
                                    this
                                )
                            }

                            composable<Cart> {
                                shouldShowBottomNav.value = true
                                CartScreen(navController, cartViewModel)
                            }

                            composable<Notification> {
                                SideEffect {
                                    shouldShowBottomNav.value = true
                                }
                                NotificationsScreen(navController, notificationsViewModel)
                            }

                            composable<Favourite> {
                                shouldShowBottomNav.value = true
                                FavouritesScreen(navController, this)
                            }

                            composable<AddressList> {
                                shouldShowBottomNav.value = false
                                AddressScreen(
                                    navController = navController,
                                )
                            }

                            composable<AddAddress> {
                                shouldShowBottomNav.value = false

                                AddAddressScreen(
                                    navController = navController,
                                )
                            }

                            composable<OrderSuccess>{
                                shouldShowBottomNav.value = false
                                val route = it.toRoute<OrderSuccess>()
                                OrderSuccessScreen(
                                    orderID = route.orderId,
                                    navController = navController
                                )
                            }

                            composable<OrderList> {
                                shouldShowBottomNav.value = true
                                OrderListScreen(
                                    navController = navController
                                )
                            }

                            composable<OrderDetails> {
                                SideEffect {
                                    shouldShowBottomNav.value = false
                                }
                                val orderID = it.toRoute<OrderDetails>().orderId
                                OrderDetailsScreen(
                                    navController = navController,
                                    orderID = orderID
                                )
                            }

                            composable<ContactUs> {
                                shouldShowBottomNav.value = false
                                ContactUsScreen(navController)
                            }

                            composable<Setting> {
                                shouldShowBottomNav.value = false
                                SettingsScreen(navController)
                            }

                            composable<HelpAndSupport> {
                                shouldShowBottomNav.value = false
                                HelpSupportScreen(navController)
                            }

                            composable<PaymentMethod> {
                                shouldShowBottomNav.value = false
                                PaymentMethodsScreen(navController)
                            }

                            composable<Profile> {
                                shouldShowBottomNav.value = false
                                ProfileScreen(navController)
                            }

                        }
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            showSplashScreen = false
            processIntent(intent, viewModel)
        }
    }

}




@Composable
fun BoxScope.ItemCount(count: Int) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(BrightYellow)
            .align(Alignment.TopEnd)
    ) {
        Text(
            text = "$count",
            color = Color.White,
            style = TextStyle(fontSize = 10.sp),
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun SetStatusBarLight(isLight: Boolean) {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view)?.isAppearanceLightStatusBars = isLight
    }
}
