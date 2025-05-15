package com.amos_tech_code.foodhub.ui.feature.order_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.data.model.response.Order
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.LoadingScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderDetails
import kotlinx.coroutines.launch

@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel()
) {

    val listOfItems = viewModel.getOrderTypes()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Order List",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium
        )

        val pagerState = rememberPagerState(pageCount = { listOfItems.size})
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(key1 = pagerState.currentPage) {
            viewModel.getOrdersByType(listOfItems[pagerState.currentPage])
        }

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
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
            Column {

                val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                when (uiState.value) {
                    OrderListViewModel.OrdersScreenState.Failed -> {
                        ErrorScreen(
                            message = "Failed to load data",
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
                        LazyColumn {
                            items(orders) { order ->
                                OrderListItem(
                                    order = order,
                                    onOrderClicked = { navController.navigate(OrderDetails(order.id))}
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .clickable {
                onOrderClicked()
            }
            .padding(8.dp)
    ) {
        Text(text = order.id)
        Text(text = order.status)
        Text(text = order.address.addressLine1)
    }
}