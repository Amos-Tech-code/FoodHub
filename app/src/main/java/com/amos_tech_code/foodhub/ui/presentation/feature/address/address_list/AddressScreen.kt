package com.amos_tech_code.foodhub.ui.presentation.feature.address.address_list

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.ui.presentation.feature.cart.AddressCard
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
            }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrowback),
                contentDescription = "Back",
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
            Text(text = "Address List", style = MaterialTheme.typography.titleLarge)
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add",
                modifier = Modifier.clickable {
                    viewModel.onAddAddressClicked()
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
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
                    Text(text = "Loading...",style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
            is AddressViewModel.AddressUiState.Success -> {

                LazyColumn(
                    modifier = Modifier.padding(16.dp).fillMaxSize()
                ) {
                    items(state.data) { address ->
                        AddressCard(address = address, onAddressClicked = {})
                    }
                }
            }
        }
    }



}