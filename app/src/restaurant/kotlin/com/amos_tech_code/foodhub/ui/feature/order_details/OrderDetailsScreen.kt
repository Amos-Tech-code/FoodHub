package com.amos_tech_code.foodhub.ui.feature.order_details

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.LoadingScreen
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderID: String,
    navController: NavController,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = orderID) {
        viewModel.getOrderDetails(orderID)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Order Details", fontWeight = FontWeight.SemiBold)
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
                        onRetry = {
                            viewModel.getOrderDetails(orderID)
                        }
                    )
                }

                is OrderDetailsViewModel.OrderDetailsUiState.Success -> {
                    val order =
                        (uiState.value as OrderDetailsViewModel.OrderDetailsUiState.Success).order
                    Text(
                        text = "ORDER ID: ${order.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Update Order Status here",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(16.dp)
                    )
                    FlowRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        viewModel.listOfStatus.forEach {
                            Button(
                                onClick = { viewModel.updateOrderStatus(orderID, it) },
                                enabled = order.status != it
                            ) {
                                Text(text = it)
                            }
                        }
                    }
                }
            }


        }
    }
}
