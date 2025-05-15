package com.amos_tech_code.foodhub.ui.feature.orders.details

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.LoadingScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.orders.order_map.OrderTrackerMapView
import com.amos_tech_code.foodhub.utils.OrderUtils
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    navController: NavController,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = orderId) {
        viewModel.getOrderDetails(orderId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Order Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LaunchedEffect(key1 = true) {
                viewModel.event.collectLatest {
                    when (it) {
                        is OrderDetailsViewModel.OrderDetailsEvent.NavigateBack -> {
                            navController.popBackStack()
                        }

                        is OrderDetailsViewModel.OrderDetailsEvent.ShowPopUp -> {
                            Toast.makeText(navController.context, it.msg, Toast.LENGTH_SHORT).show()
                        }

                        else -> {

                        }
                    }
                }
            }

            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            when (uiState.value) {
                is OrderDetailsViewModel.OrderDetailsUiState.Loading -> {
                    LoadingScreen()
                }

                is OrderDetailsViewModel.OrderDetailsUiState.Error -> {
                    ErrorScreen(
                        message = (uiState.value as OrderDetailsViewModel.OrderDetailsUiState.Error).message,
                        onRetry = { viewModel.getOrderDetails(orderId) }
                    )
                }

                is OrderDetailsViewModel.OrderDetailsUiState.Success -> {
                    val order =
                        (uiState.value as OrderDetailsViewModel.OrderDetailsUiState.Success).order
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ORDER ID: ${order.id}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        order.items.forEach {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                                    .fillMaxWidth()
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .padding(16.dp)
                            ) {
                                Row {
                                    Text(text = "Product: ", fontWeight = FontWeight.SemiBold)
                                    Text(text = it.menuItemName ?: "")
                                }

                                Row {
                                    Text(text = "Quantity: ", fontWeight = FontWeight.SemiBold)
                                    Text(text = it.quantity.toString())
                                }
                            }
                        }

                        if (order.status == OrderUtils.OrderStatus.DELIVERED.name) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text(
                                    text = "Order Delivered",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Button(onClick = { navController.popBackStack() }) {
                                    Text(
                                        text = "Back",
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        } else {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                viewModel.listOfStatus.forEach {
                                    Button(
                                        onClick = { viewModel.updateOrderStatus(orderId, it) },
                                        enabled = order.status != it
                                    ) {
                                        Text(text = it)
                                    }
                                }
                            }
                        }
                    }

                }

                is OrderDetailsViewModel.OrderDetailsUiState.OrderDelivery -> {
                    val order =
                        (uiState.value as OrderDetailsViewModel.OrderDetailsUiState.OrderDelivery).order
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(text = "Order ID: ${ order.id }", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Button(
                                onClick = {
                                    viewModel.updateOrderStatus(
                                        orderId,
                                        OrderUtils.OrderStatus.DELIVERED.name
                                    )
                                },
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Deliver",
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                        OrderTrackerMapView(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            viewModel = viewModel,
                            order = order
                        )
                    }

                }
            }
        }
    }
}

