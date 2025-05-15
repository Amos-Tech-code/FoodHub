package com.amos_tech_code.foodhub.ui.feature.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.FillBounds
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.layout.ContentScale.Companion.Inside
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.model.response.Category
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import com.amos_tech_code.foodhub.data.model.response.Restaurant
import com.amos_tech_code.foodhub.data.model.UIFoodItem
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.FoodHubTextField
import com.amos_tech_code.foodhub.ui.presentation.LoadingScreenWithText
import com.amos_tech_code.foodhub.ui.presentation.TripleOrbitAnimation
import com.amos_tech_code.foodhub.ui.presentation.feature.common.FoodItemView
import com.amos_tech_code.foodhub.ui.presentation.navigation.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.FoodDetails
import com.amos_tech_code.foodhub.ui.presentation.navigation.Home
import com.amos_tech_code.foodhub.ui.presentation.navigation.NavigationDrawerItems
import com.amos_tech_code.foodhub.ui.presentation.navigation.Profile
import com.amos_tech_code.foodhub.ui.presentation.navigation.RestaurantsDetails
import com.amos_tech_code.foodhub.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val imageUrl = viewModel.imageUrl
    val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvents.collectLatest {
            when (it) {
                is HomeViewModel.HomeScreenNavigationEvents.NavigateToDetail -> {
                    navController.navigate(RestaurantsDetails(it.name, it.imageUrl, it.id,))
                }
                is HomeViewModel.HomeScreenNavigationEvents.NavigateToLogin -> {
                    navController.navigate(AuthScreen) {
                        popUpTo(Home) { inclusive = true }
                    }
                }
                else -> {
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerContentColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()

            ) {
                DrawerContent(
                    navController,
                    onCloseDrawer = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    onLogOutClick =  {
                        viewModel.logOut()
                    }
                )
            }
        },
        gesturesEnabled = true

    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = "menu icon",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(4.dp)
                            )
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .shadow(elevation = 16.dp, shape = CircleShape)
                                .clip(CircleShape)
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    navController.navigate(Profile)
                                }
                        ) {
                            if (imageUrl.isNullOrBlank()) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_profile),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Center),
                                    colorFilter = ColorFilter.tint(Color.White)
                                )

                            } else {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Center),
                                    contentScale = Inside
                                )
                            }
                        }
                    },

                    )
            },
        ) { innerPadding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "What would you like to order?",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            verticalAlignment = CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            FoodHubTextField(
                                value = searchQuery.value,
                                onValueChange = {
                                    viewModel.onSearchQueryChanged(it)
                                },
                                singleLine = true,
                                placeholder = { Text(text = "Search") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                    }
                                ),
                            )

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = CircleShape
                                    )
                                    .size(48.dp)
                                    .clickable {
                                        //Search through filters
                                    }
                                    .padding(8.dp),
                                contentAlignment = Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_filter),
                                    contentDescription = "filter"
                                )
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                        val popularFoodItemsState = viewModel.popularFoodItemsState.collectAsStateWithLifecycle()
                        val categories =
                            (uiState.value as? HomeViewModel.HomeScreenState.Success)?.categories
                        val restaurants =
                            (uiState.value as? HomeViewModel.HomeScreenState.Success)?.restaurants
                        val popularFoodItems =
                            (popularFoodItemsState.value as? HomeViewModel.PopularFoodItemState.Success)?.foodItems

                        when (uiState.value) {
                            is HomeViewModel.HomeScreenState.Loading -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    TripleOrbitAnimation()
                                    Text(
                                        text = "Getting Restaurants near you...",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            is HomeViewModel.HomeScreenState.Success -> {
                                if (categories.isNullOrEmpty() && restaurants.isNullOrEmpty() && popularFoodItems.isNullOrEmpty()) {
                                    ErrorScreen(
                                        message = "No Data Found \n No restaurants found near you i.e 5KM away",
                                        onRetry = { viewModel.retry() },
                                        showSupportAction = false
                                    )
                                } else {
                                    if (categories != null) {
                                        CategoriesList(
                                            categories = categories,
                                            onCategorySelected = {})
                                    }

                                    if (restaurants != null) {
                                        RestaurantList(
                                            restaurants = restaurants,
                                            animatedVisibilityScope = animatedVisibilityScope,
                                            onRestaurantSelected = {
                                                viewModel.onRestaurantSelected(it)
                                            },
                                            onFavouriteClick = {

                                            }
                                        )
                                    }

                                    when(popularFoodItemsState.value) {
                                        is HomeViewModel.PopularFoodItemState.Success -> {
                                            if (popularFoodItems != null) {
                                                FoodItemList(
                                                    foodItems = popularFoodItems,
                                                    onFoodItemSelected = {
                                                        navController.navigate(FoodDetails(UIFoodItem.fromFoodItem(it)))
                                                    },
                                                    onFavoriteClick = {
                                                    },
                                                    animatedVisibilityScope = animatedVisibilityScope
                                                )
                                                Spacer(modifier = Modifier.size(100.dp))
                                            }
                                        }
                                        is HomeViewModel.PopularFoodItemState.Loading -> {
                                            LoadingScreenWithText(
                                                text = "Loading Popular Food Items..."
                                            )
                                        }
                                        is HomeViewModel.PopularFoodItemState.Error -> {
                                            ErrorScreen(
                                                message = (popularFoodItemsState.value as HomeViewModel.PopularFoodItemState.Error).message,
                                                onRetry = { viewModel.retryPopularFoodItems() }
                                            )
                                        }
                                    }
                                }

                            }

                            is HomeViewModel.HomeScreenState.Error -> {
                                val error = (uiState.value as HomeViewModel.HomeScreenState.Error).message
                                ErrorScreen(message = error, onRetry = { viewModel.retry() })
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoriesList(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit
) {
    LazyRow {
        items(categories) {
            CategoryItem(category = it, onCategorySelected = onCategorySelected)
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantList(
    restaurants: List<Restaurant>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFavouriteClick: (Restaurant) -> Unit,
    onRestaurantSelected: (Restaurant) -> Unit
) {
    Column {
        Row {
            Text(
                text = "Featured Restaurants",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "View All", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    LazyRow {
        items(restaurants) {
            RestaurantItem(it, animatedVisibilityScope, onRestaurantSelected, onFavouriteClick)
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodItemList(
    foodItems: List<FoodItem>,
    onFoodItemSelected: (FoodItem) -> Unit,
    onFavoriteClick: (FoodItem) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column {
        Row {
            Text(
                text = "Popular Items",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "View All", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    LazyRow {
        items(foodItems) { foodItem ->
            FoodItemView(
                foodItem = foodItem,
                animatedVisibilityScope = animatedVisibilityScope,
                onFoodItemClick = onFoodItemSelected,
                onFavoriteClick = onFavoriteClick,
                modifier = Modifier.width(150.dp)
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantItem(
    restaurant: Restaurant,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRestaurantSelected: (Restaurant) -> Unit,
    onFavouriteClick: (Restaurant) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .width(250.dp)
            .height(229.dp)
            .shadow(16.dp, shape = RoundedCornerShape(16.dp))
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onRestaurantSelected(restaurant) }

    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${restaurant.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .weight(1f),
                contentScale = Crop
            )

            Column(modifier = Modifier
                .background(Color.White)
                .padding(12.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState(key = "title/${restaurant.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
                Row {
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_delivery),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(end = 8.dp)
                                .size(12.dp)
                        )
                        Text(
                            text = "Free Delivery",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_timer),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(end = 8.dp)
                                .size(12.dp)
                        )
                        Text(
                            text = "10-15 min",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .align(TopStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {
            Text(
                text = "4.5", style = MaterialTheme.typography.titleSmall,

                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Image(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Yellow)
            )
            Text(
                text = "(25)", style = MaterialTheme.typography.bodySmall, color = Color.Gray
            )
        }

        IconButton(
            onClick = { onFavouriteClick(restaurant) },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd)
                .background(color = Color.White, shape = CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = "mark as favourite",
                modifier = Modifier
                    .size(60.dp)
                    .padding(4.dp),
            )
        }
    }
}


@Composable
fun CategoryItem(
    category: Category,
    onCategorySelected: (Category) -> Unit
) {

    Column(modifier = Modifier
        .padding(8.dp)
        .height(90.dp)
        .width(60.dp)
        .shadow(
            elevation = 16.dp,
            shape = RoundedCornerShape(45.dp),
            ambientColor = Color.Gray.copy(alpha = 0.8f),
            spotColor = Color.Gray.copy(alpha = 0.8f)
        )
        .clip(RoundedCornerShape(45.dp))
        .background(color = Color.White)
        .clickable { onCategorySelected(category) }
        .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally)
    {
        AsyncImage(
            model = category.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Primary,
                    spotColor = Primary
                )
                .clip(RoundedCornerShape(20.dp)),
            contentScale = FillBounds
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = category.name,
            fontWeight = FontWeight.SemiBold,
            style = TextStyle(fontSize = 10.sp),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun DrawerContent(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    onLogOutClick: () -> Unit
) {

    val drawerItems = listOf(
        NavigationDrawerItems.MyOrders,
        NavigationDrawerItems.MyProfile,
        NavigationDrawerItems.DeliveryAddress,
        NavigationDrawerItems.PaymentMethod,
        NavigationDrawerItems.ContactUs,
        NavigationDrawerItems.Settings,
        NavigationDrawerItems.HelpAndSupport
    )

    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, top = 16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = FillWidth
            )
        }

        HorizontalDivider()

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            drawerItems.forEach { item ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            painterResource(item.icon),
                            contentDescription = null,
                            modifier = Modifier.size(27.dp)
                        )
                    },
                    label = { Text(text = item.label, fontSize = 17.sp) },
                    selected = false,
                    onClick = {
                        onCloseDrawer()
                        navController.navigate(item.route)
                    }
                )
            }
        }

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LogOut(onLogOutClick = onLogOutClick)
        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogOut(onLogOutClick: () -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { showDialog = true }
            .padding(vertical = 8.dp, horizontal = 24.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .size(28.dp)
                .padding(8.dp),
            contentAlignment = Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = "logout"
            )
        }

        Text(
            text = "LogOut",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDialog = false},
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(text = "Are you sure you want to log out?")
                Row(
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(onClick = { showDialog = false }) {
                        Text(text = "Cancel")
                    }
                    TextButton(onClick = { onLogOutClick() }) {
                        Text(text = "Log out")
                    }
                }
            }
        }
    }
}