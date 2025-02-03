package com.amos_tech_code.foodhub.ui.presentation.feature.restaurants_details

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import com.amos_tech_code.foodhub.ui.presentation.gridItems
import com.amos_tech_code.foodhub.ui.presentation.navigation.FoodDetails

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantDetailsScreen(
    navController: NavController,
    name: String,
    imgUrl: String,
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: RestaurantsDetailsViewModel = hiltViewModel(),
    ) {

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(restaurantId) {
        viewModel.getFoodItem(restaurantId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            RestaurantsDetailsHeader(
                name = name,
                imgUrl = imgUrl,
                restaurantId = restaurantId,
                animatedVisibilityScope = animatedVisibilityScope,
                onBackClick = {
                    navController.popBackStack()
                },
                onFavouriteClick = {
                }
            )
        }

        item {
            RestaurantsDetails(
                title = name,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                restaurantId = restaurantId,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }

        when (uiState.value) {
            RestaurantsDetailsViewModel.RestaurantEvent.Error -> {
                item {
                    Text(text = "Error")
                }
            }
            RestaurantsDetailsViewModel.RestaurantEvent.Loading -> {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                        Text("Loading")
                    }
                }
            }
            RestaurantsDetailsViewModel.RestaurantEvent.Nothing -> {
                item {
                    Text(text = "No menu items found for this restaurant")
                }
            }
            is RestaurantsDetailsViewModel.RestaurantEvent.Success -> {
                val foodItems =
                   (uiState.value as RestaurantsDetailsViewModel.RestaurantEvent.Success).foodItems

                if (foodItems.isNotEmpty()) {
                    gridItems(foodItems, 2) { foodItem ->
                        FoodItemView(
                            foodItem = foodItem,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onFoodItemClick = {
                                navController.navigate(FoodDetails(foodItem))
                            }
                        )
                    }
                } else {
                    item {
                        Text(text = "No menu items found for this restaurant")
                    }
                }
            }
        }

    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantsDetailsHeader(
    name: String,
    imgUrl: String,
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

//        AsyncImage(
//            model = imgUrl,
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(
//                    RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
//                ),
//            contentScale = ContentScale.Crop
//        )

        Image(
            painter = painterResource(R.drawable.restaurant_img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(
                    RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .sharedElement(
                    state = rememberSharedContentState(key = "image/${restaurantId}"),
                    animatedVisibilityScope
                ),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .align(Alignment.TopStart)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrowback),
                contentDescription = null
            )
        }

        IconButton(
            onClick = onFavouriteClick,
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = null,
            )
        }

    }

}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantsDetails(
    title: String,
    description: String,
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(16.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = "title/${restaurantId}"),
                    animatedVisibilityScope
                )
        )
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "4.5",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = "(30+)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            //Spacer(modifier = Modifier.size(8.dp))
            TextButton(
                onClick = { },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "View all Reviews",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = description,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.size(8.dp))

    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodItemView(
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFoodItemClick: (FoodItem) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(216.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Gray.copy(alpha = 0.8f),
                spotColor = Color.Gray.copy(alpha = 0.8f)
            )
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onFoodItemClick.invoke(foodItem) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(147.dp)
        ) {
//            AsyncImage(
//                model = foodItem.imageUrl,
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(RoundedCornerShape(16.dp))
//                    .aspectRatio(1f),
//                contentScale = ContentScale.Crop
//            )
            Image(
                painter = painterResource(R.drawable.fooditem_img),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${foodItem.id}"),
                        animatedVisibilityScope
                    ),
                contentScale = ContentScale.Crop,
            )

            Text(
                text = "$${foodItem.price}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopStart)
            )
            Image(
                painter = painterResource(R.drawable.ic_heart),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = "(4.5)",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "(20)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = foodItem.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "title/${foodItem.id}"),
                        animatedVisibilityScope
                    )
            )
            Text(
                text = foodItem.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1
            )
        }


    }
}