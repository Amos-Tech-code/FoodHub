package com.amos_tech_code.foodhub.ui.presentation.feature.food_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.model.response.FoodItem
import com.amos_tech_code.foodhub.ui.presentation.BasicDialog
import com.amos_tech_code.foodhub.ui.presentation.feature.restaurants_details.RestaurantsDetails
import com.amos_tech_code.foodhub.ui.presentation.feature.restaurants_details.RestaurantsDetailsHeader
import com.amos_tech_code.foodhub.ui.presentation.navigation.Cart
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    foodItem: FoodItem,
    onAddToCartClicked: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FoodDetailsViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val count = viewModel.quantity.collectAsStateWithLifecycle()
    val isLoading = remember { mutableStateOf(false) }
    val showErrorDialog = remember { mutableStateOf(false) }
    val showSuccessDialog = remember { mutableStateOf(false) }

    when (uiState.value) {
        is FoodDetailsViewModel.FoodDetailsUiState.Loading -> {
            isLoading.value = true
        }
        else -> {
            isLoading.value = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when (it) {
                is FoodDetailsViewModel.FoodDetailsNavigationEvent.GoToCart -> {
                    navController.navigate(Cart)
                }
                is FoodDetailsViewModel.FoodDetailsNavigationEvent.ShowErrorDialog -> {
                    showErrorDialog.value = true
                }
                is FoodDetailsViewModel.FoodDetailsNavigationEvent.OnAddToCart -> {
                    showSuccessDialog.value = true
                    onAddToCartClicked()
                }

                null -> {
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
            enabled = !isLoading.value,
            modifier = Modifier.padding(16.dp)
        ) {
                AnimatedVisibility(visible = !isLoading.value) {
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
                AnimatedVisibility(visible = isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }

        }
    }

    if (showSuccessDialog.value) {
        ModalBottomSheet(
            onDismissRequest = {
                //showSuccessDialog.value = false
            }
        ) {
            Column() {
                Text(
                    text = "Item added to cart successfully",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.size(16.dp))

                Button(
                    onClick = {
                        showSuccessDialog.value = false
                        viewModel.goToCart()
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Go to Cart"
                    )
                }
                Button(
                    onClick = {
                        showSuccessDialog.value = false
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "OK"
                    )
                }
            }
        }
    }
    if (showErrorDialog.value) {
        ModalBottomSheet(onDismissRequest = { showErrorDialog.value = false }) {
            BasicDialog(
                title = "Error",
                description = (uiState.value as? FoodDetailsViewModel.FoodDetailsUiState.Error)?.message
                ?: "Failed to add item to cart"
            ) {
                showErrorDialog.value = false
            }
        }
    }



}