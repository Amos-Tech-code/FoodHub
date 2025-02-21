package com.amos_tech_code.foodhub.ui.presentation.feature.address.add_address

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

@Composable
fun AddAddressScreen(
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isPermissionGranted = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is AddAddressViewModel.AddAddressEvent.NavigateToAddressList -> {
                    Toast.makeText(navController.context, "Address Added Successfully", Toast.LENGTH_SHORT).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set("isAddressAdded", true)
                    navController.popBackStack()
                }
            }
        }
    }

    RequestLocationPermission(
        onPermissionGranted = {
            isPermissionGranted.value = true
            viewModel.getLocation()
        },
        onPermissionDenied = {
            Toast.makeText(navController.context, "Permission Denied", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    )

    if (!isPermissionGranted.value) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val location = viewModel.getLocation().collectAsStateWithLifecycle(null)

            location.value?.let {
                val cameraState = rememberCameraPositionState()
                LaunchedEffect(Unit) {
                    cameraState.position =
                        CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 10f)
                }

                val centerScreenMaker = remember { mutableStateOf(LatLng(it.latitude, it.longitude)) }
                LaunchedEffect(key1 = cameraState) {
                    snapshotFlow {
                        cameraState.position.target
                    }.collectLatest {
                        centerScreenMaker.value = cameraState.position.target
                        if (!cameraState.isMoving) {
                            viewModel.reverseGeocode(
                                centerScreenMaker.value.latitude,
                                centerScreenMaker.value.longitude
                            )
                        }
                    }
                }

                GoogleMap(
                    cameraPositionState = cameraState,
                    uiSettings = MapUiSettings(
                        compassEnabled = true,
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = true
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    centerScreenMaker.value.let {
                        Marker(
                            state = MarkerState(
                                position = LatLng(it.latitude, it.longitude)
                            ),
                            title = "Current Location"
                        )
                    }
                }
            }

            val address = viewModel.address.collectAsStateWithLifecycle()
            address.value?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(8.dp)
                        .background(Color.White)
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (uiState.value is AddAddressViewModel.AddAddressUiState.Loading) {
                                CircularProgressIndicator()
                            } else if (uiState.value is AddAddressViewModel.AddAddressUiState.Error) {
                                Text(
                                    text = (uiState.value as AddAddressViewModel.AddAddressUiState.Error).message,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            else {
                                Text(
                                    text = it.addressLine1,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = "${it.city}, ${it.state}, ${it.country}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                        Button(
                            onClick = { viewModel.onAddAddressClicked() },
                        ) {
                            Text(text = "Use This Address")
                        }
                    }
                }
            }

        }

    }
}

@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current

    if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
        context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED)
    {
        onPermissionGranted()
        return
    }
    val permission = listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()) { result ->

            if (result.values.all { it }) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

    LaunchedEffect(key1 = Unit) {
        permissionLauncher.launch(permission.toTypedArray())
    }

}