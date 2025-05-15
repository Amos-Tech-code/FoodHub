package com.amos_tech_code.foodhub.ui.feature.food_details

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.model.UIFoodItem
import com.amos_tech_code.foodhub.ui.feature.restaurants_details.RestaurantsDetails
import com.amos_tech_code.foodhub.ui.feature.restaurants_details.RestaurantsDetailsHeader
import com.amos_tech_code.foodhub.ui.presentation.BasicDialog
import com.amos_tech_code.foodhub.ui.presentation.navigation.Cart
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    foodItem: UIFoodItem,
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

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        var selectedAddOnFoodItemId by remember { mutableStateOf<String?>(null) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box {
                Column {
                    RestaurantsDetailsHeader(
                        name = foodItem.name,
                        imgUrl = foodItem.imageUrl,
                        restaurantId = foodItem.id,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onFavouriteClick = {},
                        onBackClick = { navController.navigateUp() }
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
                            .padding(horizontal = 16.dp)
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
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .clickable { viewModel.decrementQuantity() }
                            )
                            Text(
                                text = "${count.value}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .clickable { viewModel.incrementQuantity() }
                            )
                        }

                    }
                    //Choice of Add Ons
                    Column(
                        modifier = Modifier
                            //.height(200.dp)
                            .padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth()
                    ) {
                        Text(
                            text = "Choice of Add On",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        LazyColumn {
                            items(sampleFoodItems) { foodItem ->
                                AddOnItem(
                                    foodItem = foodItem,
                                    selectedId = selectedAddOnFoodItemId,
                                    onSelected = {
                                        selectedAddOnFoodItemId = it
                                    }
                                )
                            }
                            item {
                                Spacer( modifier = Modifier.size(100.dp))
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.addToCart(
                            restaurantId = foodItem.restaurantId,
                            foodItemId = foodItem.id
                        )
                    },
                    enabled = !isLoading.value,
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = "Adding to cart...")
                        }
                    }

                }

           }
        }
    }

    if (showSuccessDialog.value) {
        ModalBottomSheet(
            onDismissRequest = {
                //showSuccessDialog.value = false
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
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


@Composable
fun AddOnItem(
    foodItem: UIFoodItem,
    selectedId: String?,
    onSelected: (String?) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(8.dp).height(50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            AsyncImage(
                model = foodItem.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = foodItem.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Text(
            text = "$${foodItem.price}",
            style = MaterialTheme.typography.titleMedium
        )

        RadioButton(
            selected = selectedId == foodItem.id,
            onClick = {
                if (selectedId == foodItem.id) {
                    onSelected(null)
                } else {
                    onSelected(foodItem.id)
                }
            }
        )
    }
}

val sampleFoodItems = listOf(
    UIFoodItem(
        arModelUrl = "https://example.com/ar_models/burger_model.glb",
        createdAt = "2025-04-27T12:00:00Z",
        description = "A juicy beef burger topped with cheddar cheese, lettuce, and tomato.",
        id = "food_001",
        imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        name = "Cheeseburger Deluxe",
        price = 8.99,
        restaurantId = "resto_001"
    ),
    UIFoodItem(
        arModelUrl = "https://example.com/ar_models/pizza_model.glb",
        createdAt = "2025-04-26T15:30:00Z",
        description = "Thin-crust pizza with pepperoni, mozzarella, and homemade tomato sauce.",
        id = "food_002",
        imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?q=80&w=2076&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        name = "Pepperoni Pizza",
        price = 12.50,
        restaurantId = "resto_002"
    ),
    UIFoodItem(
        arModelUrl = "https://example.com/ar_models/sushi_model.glb",
        createdAt = "2025-04-25T18:45:00Z",
        description = "Fresh salmon and avocado wrapped in premium sushi rice and seaweed.",
        id = "food_003",
        imageUrl = "https://images.unsplash.com/photo-1553621042-f6e147245754?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        name = "Salmon Avocado Roll",
        price = 14.25,
        restaurantId = "resto_003"
    ),
    UIFoodItem(
        arModelUrl = "https://example.com/ar_models/salad_model.glb",
        createdAt = "2025-04-24T10:20:00Z",
        description = "A healthy green salad with kale, avocado, cherry tomatoes, and vinaigrette.",
        id = "food_004",
        imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        name = "Avocado Kale Salad",
        price = 9.75,
        restaurantId = "resto_004"
    )
)
