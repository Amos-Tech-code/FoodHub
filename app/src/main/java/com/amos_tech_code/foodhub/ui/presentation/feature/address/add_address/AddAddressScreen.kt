package com.amos_tech_code.foodhub.ui.presentation.feature.address.add_address

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AddAddressScreen(
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel()
) {

    Column{
        val cameraState = rememberCameraPositionState()
        cameraState.position = CameraPosition.fromLatLngZoom(LatLng(-0.78392810, 37.04003390), 10f)

        GoogleMap(
            cameraPositionState = cameraState,
            modifier = Modifier.fillMaxSize()
        )
    }
}