package com.amos_tech_code.foodhub.ui.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NoMeals
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.amos_tech_code.foodhub.ui.feature.profile.LogOut
import com.amos_tech_code.foodhub.ui.presentation.EmptyState
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.LoadingScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.Home
import com.amos_tech_code.foodhub.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesScreen(
    navController: NavController,
    viewModel: DeliveriesViewModel = hiltViewModel()
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        viewModel.deliveriesEvent.collectLatest {
            when (it) {
                is DeliveriesViewModel.DeliveriesEvent.NavigateToLogin -> {
                    navController.navigate(AuthScreen) {
                        popUpTo(Home) {
                            inclusive = true
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Deliveries",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Pull down to refresh",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isDropdownExpanded = true
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "More"
                        )
                    }
                    //Open drop down
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = {
                            isDropdownExpanded = false
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = "Settings")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = "Settings icon"
                                    )
                                },
                                onClick = {
                                    isDropdownExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LogOut(
                                onLogOutClick = {
                                    isDropdownExpanded = false
                                    viewModel.logOut()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val uiState = viewModel.deliveriesState.collectAsStateWithLifecycle()
            var isRefreshing by remember { mutableStateOf(false) }
            val pullToRefreshState = rememberPullToRefreshState()

            when (val state = uiState.value) {
                is DeliveriesViewModel.DeliveriesState.Loading -> {
                    LoadingScreen()
                }

                is DeliveriesViewModel.DeliveriesState.Success -> {

                        PullToRefreshBox(
                            state = pullToRefreshState,
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                //isRefreshing = true
                                viewModel.getDeliveries()
                            },
                        ) {

                            if (state.deliveries.isEmpty()) {
                                EmptyState(
                                    title = "No deliveries found",
                                    description = "You have no deliveries at the moment\n" +
                                            "Pull down to refresh",
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Rounded.NoMeals,
                                            contentDescription = null
                                        )
                                    },
                                )
                            } else {
                                LazyColumn {
                                    items(state.deliveries) { delivery ->

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = "Customer Address: ${delivery.customerAddress}",
                                                color = Color.Black,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                text = "Restaurant: ${delivery.restaurantAddress}",
                                                color = Color.Black,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                text = "Order ID: ${delivery.orderId}",
                                                color = Color.Gray,
                                                minLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "${delivery.estimatedDistance} KM",
                                                color = Color.Black,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = StringUtils.formatCurrency(delivery.estimatedEarning),
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.titleLarge
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Button(onClick = {
                                                    viewModel.deliveryAccepted(
                                                        delivery
                                                    )
                                                }) {
                                                    Text(text = "Accept")
                                                }
                                                OutlinedButton(
                                                    onClick = { viewModel.deliveryRejected(delivery) },
                                                    border = BorderStroke(
                                                        1.dp,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                ) {
                                                    Text(text = "Decline")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                }

                is DeliveriesViewModel.DeliveriesState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.getDeliveries() }
                    )
                }
            }
        }
    }


}