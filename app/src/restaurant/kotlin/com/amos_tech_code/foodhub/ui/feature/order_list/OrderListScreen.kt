package com.amos_tech_code.foodhub.ui.feature.order_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.data.model.response.Order
import com.amos_tech_code.foodhub.ui.presentation.EmptyState
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.LoadingScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderDetails
import com.amos_tech_code.foodhub.utils.OrderUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel()
) {

    val listOfItems = viewModel.getOrderTypes()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Order List",
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                modifier = Modifier.height(64.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {

            val pagerState = rememberPagerState(pageCount = { listOfItems.size })
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = pagerState.currentPage) {
                viewModel.getOrdersByType(listOfItems[pagerState.currentPage])
            }

            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = -(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOfItems.forEachIndexed { index, item ->
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                            .padding(8.dp)
                    )
                }
            }
            HorizontalPager(state = pagerState) { page ->

                val currentStatusFilter = listOfItems[page]
                Column {

                    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                    when (uiState.value) {
                        is OrderListViewModel.OrdersScreenState.Failed -> {
                            ErrorScreen(
                                message = (uiState.value as OrderListViewModel.OrdersScreenState.Failed).message,
                                onRetry = {
                                    viewModel.getOrdersByType(listOfItems[pagerState.currentPage])
                                }
                            )
                        }

                        OrderListViewModel.OrdersScreenState.Loading -> {
                            LoadingScreen()
                        }

                        is OrderListViewModel.OrdersScreenState.Success -> {
                            val orders =
                                (uiState.value as OrderListViewModel.OrdersScreenState.Success).data
                            if (orders.isNotEmpty()) {
                                LazyColumn {
                                    items(orders) { order ->
                                        OrderListItem(
                                            order = order,
                                            onOrderClicked = { navController.navigate(OrderDetails(order.id)) }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(56.dp))
                                    }
                                }
                            } else {
                                EmptyState(
                                    title = "No orders found with ${currentStatusFilter} order status.",
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Info Icon"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun OrderListItem(order: Order, onOrderClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp) // Standard padding
            .clickable { onOrderClicked() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Subtle shadow
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Use surface color for cards
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp) // Inner padding for content
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order ID #${order.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    minLines = 1
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Placed on: ${order.createdAt}", // Example: include order date
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector =
                    // Example icon based on status or type
                    when (order.status) {
                        OrderUtils.OrderStatus.PENDING_ACCEPTANCE.name -> Icons.Default.WatchLater
                        OrderUtils.OrderStatus.PREPARING.name -> Icons.Default.AccessTimeFilled
                        OrderUtils.OrderStatus.READY.name -> Icons.Default.LocalShipping
                        OrderUtils.OrderStatus.ACCEPTED.name -> Icons.Default.LocalShipping
                        OrderUtils.OrderStatus.DELIVERED.name -> Icons.Default.CheckCircle
                        else -> Icons.Default.LocalShipping
                    }
                    ,
                    contentDescription = "Order Status Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = " ${order.status.uppercase()}", // Uppercase for emphasis
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.address.addressLine1,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${order.address.city}, ${order.address.zipCode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Total: $${
                        String.format(
                            "%.2f",
                            order.totalAmount
                        )
                    }", // Example: include total
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}