package com.amos_tech_code.foodhub

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.amos_tech_code.foodhub.data.FoodHubSession
import com.amos_tech_code.foodhub.ui.feature.home.DeliveriesScreen
import com.amos_tech_code.foodhub.ui.feature.orders.details.OrderDetailsScreen
import com.amos_tech_code.foodhub.ui.feature.orders.list.OrderListScreen
import com.amos_tech_code.foodhub.ui.feature.profile.ProfileScreen
import com.amos_tech_code.foodhub.ui.presentation.FoodHubNavHost
import com.amos_tech_code.foodhub.ui.presentation.feature.auth.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.auth.login.LoginScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.auth.signup.SignUpScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.NotificationsScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.NotificationsViewModel
import com.amos_tech_code.foodhub.ui.presentation.navigation.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.Home
import com.amos_tech_code.foodhub.ui.presentation.navigation.Login
import com.amos_tech_code.foodhub.ui.presentation.navigation.NavItems
import com.amos_tech_code.foodhub.ui.presentation.navigation.Notification
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderDetails
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderList
import com.amos_tech_code.foodhub.ui.presentation.navigation.Profile
import com.amos_tech_code.foodhub.ui.presentation.navigation.SignUp
import com.amos_tech_code.foodhub.ui.theme.BrightYellow
import com.amos_tech_code.foodhub.ui.theme.FoodHubTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                    NavItems.Orders,
                    NavItems.Notifications,
                    NavItems.Profile
                )
                val navController = rememberNavController()
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
                                                //restoreState = true
                                            }
                                        },
                                        icon = {
                                            Box(modifier = Modifier.size(48.dp)) {
                                                Icon(
                                                    painter = painterResource(item.icon),
                                                    contentDescription = null,
                                                    tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
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
                                SignUpScreen(navController, false)
                            }

                            composable<Login> {
                                shouldShowBottomNav.value = false
                                LoginScreen(navController, false)
                            }

                            composable<AuthScreen> {
                                shouldShowBottomNav.value = false
                                AuthScreen(navController, false)

                            }

                            composable<Home> {
                                shouldShowBottomNav.value = true
                                DeliveriesScreen(navController)
                            }

                            composable<OrderList> {
                                shouldShowBottomNav.value = true
                                OrderListScreen(navController)
                            }

                            composable<OrderDetails> {
                                shouldShowBottomNav.value = false
                                val orderID = it.toRoute<OrderDetails>().orderId
                                OrderDetailsScreen(navController = navController, orderId = orderID)
                            }

                            composable<Notification> {
                                SideEffect {
                                    shouldShowBottomNav.value = true
                                }
                                NotificationsScreen(navController, notificationsViewModel)
                            }

                            composable<Profile> {
                                shouldShowBottomNav.value = true
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
