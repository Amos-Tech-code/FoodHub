package com.amos_tech_code.foodhub.ui.feature.order_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.ui.feature.orders.OrderDetailsText
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.orders.order_map.OrderTrackerMapView
import com.amos_tech_code.foodhub.utils.OrderUtils
import com.amos_tech_code.foodhub.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    navController: NavController,
    orderID: String,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = orderID) {
        viewModel.getOrderDetails(orderID)
    }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is OrderDetailsViewModel.OrderDetailsEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Orders Details", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {

            val uiState = viewModel.state.collectAsStateWithLifecycle()
            when (uiState.value) {
                is OrderDetailsViewModel.OrderDetailsState.Loading -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                        Text(text = "Loading")
                    }
                }

                is OrderDetailsViewModel.OrderDetailsState.OrderDetails -> {
                    val order =
                        (uiState.value as OrderDetailsViewModel.OrderDetailsState.OrderDetails).order
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        OrderDetailsText(order)
                        Row {
                            Text(text = "Price:", fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(text = StringUtils.formatCurrency(order.totalAmount))
                        }
                        Row {
                            Text(text = "Date:", fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(text = order.createdAt)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = viewModel.getImage(order)),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(text = order.status, fontWeight = FontWeight.SemiBold)
                        }

                        if (order.status == OrderUtils.OrderStatus.OUT_FOR_DELIVERY.name) {
                            OrderTrackerMapView(
                                modifier = Modifier,
                                viewModel = viewModel,
                                order = order
                            )
                        }
                    }

                }

                is OrderDetailsViewModel.OrderDetailsState.Error -> {
                    ErrorScreen(
                        message = (uiState.value as OrderDetailsViewModel.OrderDetailsState.Error).message,
                        onRetry = {
                            viewModel.getOrderDetails(orderID)
                        }
                    )
                }
            }

        }
    }
}