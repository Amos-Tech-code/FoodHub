package com.amos_tech_code.foodhub.ui.feature.address.add_address

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.data.model.Address
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AddAddressScreen(
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val address by viewModel.address.collectAsStateWithLifecycle()
    val isLoading by remember { derivedStateOf {
        uiState is AddAddressViewModel.AddAddressUiState.Loading ||
                uiState is AddAddressViewModel.AddAddressUiState.AddressStoring
    } }
    val context = LocalContext.current

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                AddAddressViewModel.AddAddressEvent.NavigateToAddressList -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "isAddressAdded", true
                    )
                    navController.popBackStack()
                }
                is AddAddressViewModel.AddAddressEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Permission handling
    var showPermissionRationale by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.all { it.value } -> { /* Permissions granted */ }
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == false -> {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    showPermissionRationale = true
                } else {
                    Toast.makeText(context, "Location permission denied", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }
            }
        }
    }

    // Check initial permissions
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Permission rationale dialog
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Location Permission Needed") },
            text = { Text("This app needs location permissions to find addresses near you") },
            confirmButton = {
                Button(onClick = {
                    showPermissionRationale = false
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionRationale = false
                    navController.popBackStack()
                }) {
                    Text("Deny")
                }
            }
        )
        return
    }

    // Main content
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        viewModel.getLocation().collectAsStateWithLifecycle(null).value?.let { location ->
            val cameraState = rememberCameraPositionState()
            val markerState = remember { MarkerState() }

            // Initialize camera position
            LaunchedEffect(location) {
                cameraState.position = CameraPosition.fromLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    15f // Higher zoom level for better precision
                )
                markerState.position = LatLng(location.latitude, location.longitude)
            }

            // Handle map movements
            LaunchedEffect(cameraState.isMoving) {
                if (!cameraState.isMoving) {
                    markerState.position = cameraState.position.target
                    viewModel.reverseGeocode(
                        cameraState.position.target.latitude,
                        cameraState.position.target.longitude
                    )
                }
            }

            // Map UI
            GoogleMap(
                cameraPositionState = cameraState,
                properties = MapProperties(
                    isMyLocationEnabled = true
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true,
                    compassEnabled = true
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Marker(
                    state = markerState,
                    title = "Selected Location"
                )
            }
        }

        // Address bottom sheet
        AddressBottomSheet(
            address = address,
            uiState = uiState,
            onAddClick = { viewModel.onAddAddressClicked() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
private fun AddressBottomSheet(
    address: Address?,
    uiState: AddAddressViewModel.AddAddressUiState,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when {
                address == null -> {
                    if (uiState is AddAddressViewModel.AddAddressUiState.Error) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    Text(
                        text = address.addressLine1,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${address.city}, ${address.state}, ${address.country}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onAddClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is AddAddressViewModel.AddAddressUiState.AddressStoring
                    ) {
                        if (uiState is AddAddressViewModel.AddAddressUiState.AddressStoring) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Use This Address")
                        }
                    }
                }
            }
        }
    }
}