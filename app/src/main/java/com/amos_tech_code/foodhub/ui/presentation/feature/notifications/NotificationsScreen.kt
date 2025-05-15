package com.amos_tech_code.foodhub.ui.presentation.feature.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.data.model.response.Notification
import com.amos_tech_code.foodhub.ui.presentation.EmptyState
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderDetails
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is NotificationsViewModel.NotificationEvent.NavigateToOrderDetails -> {
                    navController.navigate(OrderDetails(it.orderID))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {

            when (uiState.value) {
                is NotificationsViewModel.NotificationUIState.Loading -> {
                    LoadingScreen()
                }

                is NotificationsViewModel.NotificationUIState.Error -> {
                    val message =
                        (uiState.value as NotificationsViewModel.NotificationUIState.Error).message
                    ErrorScreen(
                        message = message,
                        onRetry = {
                            viewModel.getNotifications()
                        }
                    )
                }

                is NotificationsViewModel.NotificationUIState.Success -> {
                    val notifications =
                        (uiState.value as NotificationsViewModel.NotificationUIState.Success).data

                    if (notifications.isNotEmpty()) {
                        LazyColumn {
                            items(notifications, key = { it.id }) {
                                NotificationItem(it) {
                                    viewModel.readNotifications(it)
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.size(76.dp))
                            }
                        }
                    } else {
                        EmptyState(
                            title = "No notifications found",
                            description = "When you have new notifications, they'll appear here",
                            action = {
                                Button(onClick = { viewModel.getNotifications() }) {
                                    Text("Refresh")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun NotificationItem(
    notification: Notification,
    onRead: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(0.5.dp, MaterialTheme.colorScheme.outline)
            .background(
                if (notification.isRead) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.primaryContainer
            )
            .clickable { onRead() }
            .padding(16.dp)
    ) {
        Text(
            text = notification.title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = notification.message,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = notification.createdAt,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}