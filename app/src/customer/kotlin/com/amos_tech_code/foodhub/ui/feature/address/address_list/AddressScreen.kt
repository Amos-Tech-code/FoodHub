package com.amos_tech_code.foodhub.ui.feature.address.address_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.ui.feature.cart.AddressCard
import com.amos_tech_code.foodhub.ui.presentation.navigation.AddAddress
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddressScreen(
    navController: NavController,
    viewModel: AddressViewModel = hiltViewModel()
) {
    val uiSate = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is AddressViewModel.AddressEvent.NavigateToEditAddress -> {
                }
                is AddressViewModel.AddressEvent.NavigateToAddAddress -> {
                    navController.navigate(AddAddress)
                }
                is AddressViewModel.AddressEvent.ShowErrorDialog -> {

                }

                is AddressViewModel.AddressEvent.NavigateBack -> {
                    val address = it.address
                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedAddress", address)
                    navController.popBackStack()
                }
            }
        }
    }

    val isAddressAdded =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("isAddressAdded", false)?.collectAsState()

    LaunchedEffect(key1 = isAddressAdded?.value) {
        if (isAddressAdded?.value == true) {
            viewModel.getAddress()
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(text = "Address List", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleLarge)

                IconButton(
                    onClick = { viewModel.onAddAddressClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddCircle,
                        contentDescription = "Add Address",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            when (val state = uiSate.value) {
                is AddressViewModel.AddressUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.message, style = MaterialTheme.typography.bodyMedium)
                        Button(onClick = { viewModel.getAddress() }) {
                            Text(text = "Retry")
                        }
                    }
                }

                is AddressViewModel.AddressUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                is AddressViewModel.AddressUiState.Success -> {

                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        items(state.data) { address ->
                            AddressCard(
                                address = address,
                                onAddressClicked = {
                                    viewModel.onAddressSelected(
                                        address
                                    )
//                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedAddress", address)
//                                navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }



}