package com.amos_tech_code.foodhub.ui.feature.favourites

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import com.amos_tech_code.foodhub.data.model.response.Restaurant
import com.amos_tech_code.foodhub.ui.feature.home.RestaurantItem
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.FoodDetails
import com.amos_tech_code.foodhub.ui.presentation.navigation.RestaurantsDetails
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FavouritesScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FavouritesViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is FavouritesViewModel.FavouritesEvent.NavigateToRestaurantDetailsScreen -> {
                    navController.navigate(
                        RestaurantsDetails(
                            restaurantId = it.restaurantId,
                            imgUrl = it.restaurantImgUrl,
                            name = it.restaurantName
                        ))
                }
                is FavouritesViewModel.FavouritesEvent.NavigateToFoodDetailsScreen -> {
                    navController.navigate(FoodDetails(it.foodItem))
                }
            }
        }

    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Favourites", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()

            val listOfTabs = listOf("Restaurants", "Food Items")
            val coroutineScope = rememberCoroutineScope()
            val pagerState = rememberPagerState(pageCount = { listOfTabs.size }, initialPage = 0)

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(4.dp),
                indicator = {},
                divider = {}) {
                listOfTabs.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                color = if (pagerState.currentPage == index) Color.White else Color.Gray
                            )
                        }, selected = pagerState.currentPage == index, onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }, modifier = Modifier
                            .clip(
                                RoundedCornerShape(32.dp)
                            )
                            .background(
                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.White
                            )
                    )
                }
            }

            HorizontalPager(state = pagerState) {

                when (uiState.value) {
                    is FavouritesViewModel.FavouritesState.Loading -> {
                        // Show loading
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator()
                            Text(text = "Loading")
                        }
                    }

                    is FavouritesViewModel.FavouritesState.Favourites -> {
                        val favoriteRestaurants = viewModel.favouriteRestaurants
                        val favoriteFoodItems = viewModel.favouriteFoodItems
                        if (favoriteRestaurants.isEmpty() && favoriteFoodItems.isEmpty()) {
                            // Show empty
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(text = "No favourites items or restaurants found")
                            }
                        } else {

                            when (it) {
                                0 -> {
                                    FavouriteRestaurantList(favoriteRestaurants, animatedVisibilityScope) { favoriteRestaurant->
                                        viewModel.navigateToRestaurantDetails(favoriteRestaurant)
                                    }
                                }

                                1 -> {
                                    FavouriteFoodList(
                                        foodItems = favoriteFoodItems,
                                        onFoodItemClicked = { foodItem ->
                                            viewModel.navigateToFoodDetails(foodItem)
                                        },
                                        onFavouriteClicked = { foodItem ->
                                        }

                                    )
                                }
                            }
                        }
                    }


                    is FavouritesViewModel.FavouritesState.Error -> {
                        // Show error
                        ErrorScreen(
                            message = (uiState.value as FavouritesViewModel.FavouritesState.Error).message,
                            onRetry = {
                                viewModel.getFavourites()
                            }
                        )
                    }

                }
            }

        }
    }

}


@Composable
fun FavouriteFoodList(
    foodItems: List<FoodItem>,
    onFoodItemClicked: (FoodItem) -> Unit,
    onFavouriteClicked: (FoodItem) -> Unit
) {

    LazyColumn {
        items(foodItems) { foodItem ->
            FavouriteFoodItem(
                foodItem = foodItem,
                onFavouriteClicked = {
                    // Handle favourite click
                    onFavouriteClicked(foodItem)
                },
                onFoodItemClicked = {
                    // Handle food item click
                    onFoodItemClicked(foodItem)
                }
            )
        }
    }

}


@Composable
fun FavouriteFoodItem(
    foodItem: FoodItem,
    onFavouriteClicked: (FoodItem) -> Unit,
    onFoodItemClicked: (FoodItem) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onFoodItemClicked(foodItem) }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .height(150.dp)
        ) {
            AsyncImage(
                model = foodItem.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = foodItem.price.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.TopEnd)
                    .clickable { onFavouriteClicked(foodItem) }
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_heart),
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp).align(Alignment.Center)
                )
            }
            
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .align(Alignment.BottomStart)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text ="4.5",
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.Yellow
                    )
                    Text(
                        text = "(25+)",
                        color = Color.Gray,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = foodItem.name,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = foodItem.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FavouriteRestaurantList(
    restaurants: List<Restaurant>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRestaurantSelected: (Restaurant) -> Unit
) {
    LazyColumn {
        items(restaurants) { restaurant ->
            RestaurantItem(
                restaurant = restaurant,
                animatedVisibilityScope = animatedVisibilityScope,
                onRestaurantSelected = {
                    onRestaurantSelected(restaurant)
                },
                onFavouriteClick = {
                    // Handle favourite click
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

