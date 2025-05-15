package com.amos_tech_code.foodhub.ui.feature.orders.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.ui.presentation.EmptyState
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.LoadingScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderDetails
import com.amos_tech_code.foodhub.utils.StringUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Orders",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val state = viewModel.state.collectAsStateWithLifecycle()

            when (state.value) {
                is OrderListViewModel.OrdersListState.Loading -> {
                    LoadingScreen()
                }

                is OrderListViewModel.OrdersListState.Empty -> {
                    EmptyState(
                        title = "",
                        description = "No orders available",
                        action = {
                            // Handle the action if needed
                        }
                    )
                }

                is OrderListViewModel.OrdersListState.Success -> {
                    val orders = (state.value as OrderListViewModel.OrdersListState.Success).orders
                    LazyColumn {
                        items(orders) { delivery ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .clickable {
                                        navController.navigate(OrderDetails(delivery.orderId))
                                    }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Customer Address: ${ delivery.customer.addressLine1 }",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Restaurant: ${ delivery.restaurant.address }",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Order ID: ${ delivery.orderId }",
                                    color = Color.Gray,
                                    minLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = delivery.status,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = StringUtils.formatCurrency(delivery.estimatedEarning),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }

                is OrderListViewModel.OrdersListState.Error -> {
                    ErrorScreen(
                        message = (state.value as OrderListViewModel.OrdersListState.Error).message,
                        onRetry = { viewModel.getOrders() }
                    )
                }
            }
        }
    }

}