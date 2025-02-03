package com.amos_tech_code.foodhub.ui.presentation.feature.food_details

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import com.amos_tech_code.foodhub.ui.presentation.feature.restaurants_details.RestaurantsDetails
import com.amos_tech_code.foodhub.ui.presentation.feature.restaurants_details.RestaurantsDetailsHeader
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FoodDetailsViewModel
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val count = viewModel.quantity.collectAsStateWithLifecycle()
    val isLoading = remember { mutableStateOf(false) }

    when (uiState.value) {
        is FoodDetailsViewModel.FoodDetailsUiState.Loading -> {
            isLoading.value = false
        }
        else -> {
            isLoading.value = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when (it) {
                is FoodDetailsViewModel.FoodDetailsNavigationEvent.GoToCart -> {

                }
                is FoodDetailsViewModel.FoodDetailsNavigationEvent.ShowErrorDialog -> {
                    Toast.makeText(
                        navController.context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is FoodDetailsViewModel.FoodDetailsNavigationEvent.OnAddToCart -> {
                    Toast.makeText(
                        navController.context,
                        "Item added to Cart Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                null -> {
                    TODO()
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        RestaurantsDetailsHeader(
            name = foodItem.name,
            imgUrl = foodItem.imageUrl,
            restaurantId = foodItem.id,
            animatedVisibilityScope = animatedVisibilityScope,
            onFavouriteClick = {},
            onBackClick = { navController.popBackStack() }
        )

        RestaurantsDetails(
            title = foodItem.name,
            description = foodItem.description,
            restaurantId = foodItem.id,
            animatedVisibilityScope = animatedVisibilityScope
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "$${foodItem.price}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_minus),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { viewModel.decrementQuantity() }
                )
                Text(
                    text = "${count.value}",
                    style = MaterialTheme.typography.titleMedium
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { viewModel.incrementQuantity() }
                )
            }

        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                viewModel.addToCart(
                    restaurantId = foodItem.restaurantId,
                    foodItemId = foodItem.id
                )
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_cart),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Add to cart".uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}