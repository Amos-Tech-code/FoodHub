package com.amos_tech_code.foodhub.ui.presentation.feature.home

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.Inside
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.model.response.Category
import com.amos_tech_code.foodhub.data.model.response.Restaurant
import com.amos_tech_code.foodhub.ui.presentation.TripleOrbitAnimation
import com.amos_tech_code.foodhub.ui.presentation.navigation.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.Home
import com.amos_tech_code.foodhub.ui.presentation.navigation.Login
import com.amos_tech_code.foodhub.ui.presentation.navigation.RestaurantsDetails
import com.amos_tech_code.foodhub.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
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


    Column(modifier = Modifier.fillMaxSize()) {
        val uiState = viewModel.uiState.collectAsState()
        when (uiState.value) {
            is HomeViewModel.HomeScreenState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Center
                ) {
                    TripleOrbitAnimation()
                    Text(
                        modifier = Modifier.align(TopCenter),
                        text = "Loading...",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            is HomeViewModel.HomeScreenState.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(text = "Empty Api Response")
                    TextButton(onClick = { viewModel.retry() }) {
                        Text(text = "Retry")
                    }
                }
            }

            is HomeViewModel.HomeScreenState.Success -> {
                val categories = viewModel.categories
                CategoriesList(categories = categories, onCategorySelected = {})

                RestaurantList(
                    restaurants = viewModel.restaurants,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onRestaurantSelected = {
                        viewModel.onRestaurantSelected(it)
                    }
                )

                LogOut {
                    viewModel.logOut()
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
    onRestaurantSelected: (Restaurant) -> Unit
) {
    Column {
        Row {
            Text(
                text = "Popular Restaurants",
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
            RestaurantItem(it, animatedVisibilityScope, onRestaurantSelected)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantItem(
    restaurant: Restaurant,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRestaurantSelected: (Restaurant) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(250.dp)
            .height(229.dp)
            .shadow(16.dp, shape = RoundedCornerShape(16.dp))
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onRestaurantSelected(restaurant) }

    ) {
        Column(modifier = Modifier.fillMaxSize()) {
//            AsyncImage(
//                model = restaurant.imageUrl,
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .weight(1f),
//                contentScale = androidx.compose.ui.layout.ContentScale.Crop
//            )
            Image(
                painter = painterResource(id = R.drawable.restaurant_img),
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
                Row() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
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
                        verticalAlignment = Alignment.CenterVertically,
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
                            text = "Free Delivery",
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
            verticalAlignment = Alignment.CenterVertically,
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
        .clickable { onCategorySelected(category) }
        .shadow(
            elevation = 16.dp,
            shape = RoundedCornerShape(45.dp),
            ambientColor = Color.Gray.copy(alpha = 0.8f),
            spotColor = Color.Gray.copy(alpha = 0.8f)
        )
        .background(color = Color.White)
        .clip(RoundedCornerShape(45.dp))
        .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally) {
//        AsyncImage(
//            model = category.imageUrl,
//            contentDescription = null,
//            modifier = Modifier
//                .size(40.dp)
//                .shadow(
//                    elevation = 16.dp,
//                    shape = CircleShape,
//                    ambientColor = Orange,
//                    spotColor = Orange
//                )
//                .clip(CircleShape),
//            contentScale = Inside
//        )
        Image(
            painter = painterResource(R.drawable.category_img),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = Orange,
                    spotColor = Orange
                )
                .clip(CircleShape),
            contentScale = Inside
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = category.name, style = TextStyle(fontSize = 10.sp), textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LogOut(onLogOutClick: () -> Unit) {
    Spacer(modifier = Modifier.size(8.dp))
    TextButton(
        onClick = {
            onLogOutClick()
        }
    ) {
        Text(text = "Log Out")
    }
}