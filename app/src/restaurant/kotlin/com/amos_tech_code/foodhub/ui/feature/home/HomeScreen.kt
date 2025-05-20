package com.amos_tech_code.foodhub.ui.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.feature.common.LogOut
import com.amos_tech_code.foodhub.ui.presentation.feature.notifications.LoadingScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.AddMenuItem
import com.amos_tech_code.foodhub.ui.presentation.navigation.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.Home
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    var isDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                HomeViewModel.HomeScreenEvent.NavigateToLogin -> {
                    navController.navigate(AuthScreen) {
                        popUpTo(Home) {
                            inclusive = true
                        }
                    }
                }
                is HomeViewModel.HomeScreenEvent.ShowError -> {
                }
            }
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Restaurant Profile",
                            fontWeight = FontWeight.SemiBold
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

            when (uiState.value) {
                is HomeViewModel.HomeScreenState.Failed -> {
                    ErrorScreen(
                        message = (uiState.value as HomeViewModel.HomeScreenState.Failed).message,
                        onRetry = { viewModel.retry() }
                    )
                }

                is HomeViewModel.HomeScreenState.Loading -> {
                    LoadingScreen()
                }

                is HomeViewModel.HomeScreenState.Success -> {
                    val restaurant = (uiState.value as HomeViewModel.HomeScreenState.Success).data
                    AsyncImage(
                        model = restaurant.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)) {
                        Text(
                            text = restaurant.name,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(text = restaurant.address, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = restaurant.createdAt,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                    }

                    AddMenuItemExtendedFAB {
                        navController.navigate(AddMenuItem)
                    }
                }

            }
        }
    }
}


@Composable
fun AddMenuItemExtendedFAB(
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            Icon(
                Icons.Filled.RestaurantMenu, // Or Icons.Filled.Add
                contentDescription = "Add Menu Item Icon"
            )
        },
        text = { Text("Add Item") },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
}
