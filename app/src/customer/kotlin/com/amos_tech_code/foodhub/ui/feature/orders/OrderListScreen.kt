package com.amos_tech_code.foodhub.ui.feature.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amos_tech_code.foodhub.data.model.response.Order
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderDetails
import com.amos_tech_code.foodhub.utils.OrderUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navController: NavController, viewModel: OrderListViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is OrderListViewModel.OrderListEvent.NavigateToOrderDetailScreen -> {
                    navController.navigate(OrderDetails(it.order.id))
                }

                is OrderListViewModel.OrderListEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                else -> {}
            }
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Orders", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
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
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val uiState = viewModel.state.collectAsStateWithLifecycle()

            val listOfTabs = listOf("Upcoming", "History")
            val coroutineScope = rememberCoroutineScope()
            val pagerState =
                rememberPagerState(pageCount = { listOfTabs.size }, initialPage = 0)
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(4.dp),
                indicator = {},
                divider = {}) {
                listOfTabs.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                color = if (pagerState.currentPage == index) Color.White else Color.Gray
                            )
                        }, selected = pagerState.currentPage == index, onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }, modifier = Modifier
                            .clip(
                                RoundedCornerShape(32.dp)
                            )
                            .background(
                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.White
                            )
                    )
                }
            }

            HorizontalPager(state = pagerState) {

                when (uiState.value) {
                    is OrderListViewModel.OrderListState.Loading -> {
                        // Show loading
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator()
                            Text(text = "Loading")
                        }
                    }

                    is OrderListViewModel.OrderListState.OrderList -> {
                        val list =
                            (uiState.value as OrderListViewModel.OrderListState.OrderList).orderList.orders
                        if (list.isEmpty()) {
                            // Show empty
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(text = "No orders found")
                            }
                        } else {

                            val upcomingStatus = listOf(
                                OrderUtils.OrderStatus.PENDING_ACCEPTANCE.name,
                                OrderUtils.OrderStatus.ACCEPTED.name,
                                OrderUtils.OrderStatus.PREPARING.name,
                                OrderUtils.OrderStatus.READY.name,
                                OrderUtils.OrderStatus.ASSIGNED.name,
                                OrderUtils.OrderStatus.OUT_FOR_DELIVERY.name,
                            )

                            when (it) {
                                0 -> {
                                    OrderListInternal(
                                        list.filter { order -> order.status in upcomingStatus },
                                        onClick = { order ->
                                            viewModel.navigateToDetails(order)
                                        }
                                    )
                                }

                                1 -> {
                                    OrderListInternal(
                                        list.filter { order -> order.status !in upcomingStatus },
                                        onClick = { order ->
                                            viewModel.navigateToDetails(order)
                                        }
                                    )
                                }
                            }
                        }
                    }


                    is OrderListViewModel.OrderListState.Error -> {
                        // Show error
                        ErrorScreen(
                            message = (uiState.value as OrderListViewModel.OrderListState.Error).message,
                            onRetry = {
                                viewModel.getOrders()
                            }
                        )
                    }

                    else -> {}
                }
            }

        }
    }

}

@Composable
fun OrderListInternal(list: List<Order>, onClick: (Order) -> Unit) {
    if (list.isEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "No orders found")
        }
    } else {
        LazyColumn {
            items(list) { order ->
                OrderListItem(order = order, onClick = { onClick(order) })
            }
            item {
                Spacer(modifier = Modifier.size(56.dp))
            }
        }
    }
}

@Composable
fun OrderDetailsText(order: Order) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            AsyncImage(
                model = order.restaurant.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(
                    text = "#ID: ${order.id}",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(text = if(order.items.size == 1) "${order.items.size} item" else "${order.items.size} items", color = Color.Gray)
                Text(
                    text = order.restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Text(text = "Status", color = Color.Gray)
        Text(text = order.status, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
fun OrderListItem(order: Order, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .shadow(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.White)
            .padding(16.dp)
    ) {
        OrderDetailsText(order = order)
        Button(onClick = onClick) {
            Text(
                text = "View Details",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

    }
}