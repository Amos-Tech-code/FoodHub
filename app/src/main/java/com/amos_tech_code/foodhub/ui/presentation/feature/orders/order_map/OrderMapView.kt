package com.amos_tech_code.foodhub.ui.presentation.feature.orders.order_map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amos_tech_code.foodhub.data.model.response.Order
import com.amos_tech_code.foodhub.ui.presentation.feature.orders.OrderDetailsBaseViewModel
import com.amos_tech_code.foodhub.utils.OrderUtils
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun OrderTrackerMapView(
    modifier: Modifier,
    viewModel: OrderDetailsBaseViewModel,
    order: Order
) {
    val context = LocalContext.current
    val messages = viewModel.locationUpdate.collectAsStateWithLifecycle(null)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val cameraPositionState = rememberCameraPositionState()

        LaunchedEffect(key1 = messages.value != null) {
            messages.value?.let {
                val riderMarker = LatLng(it.currentLocation.latitude, it.currentLocation.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(riderMarker, 15f)
            }
        }
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            cameraPositionState = cameraPositionState
        ) {
            messages.value?.let {

                val riderMaker = LatLng(it.currentLocation.latitude, it.currentLocation.longitude)
                val riderState = rememberMarkerState(position = riderMaker)

                LaunchedEffect(key1 = riderMaker) {
                    riderState.position = riderMaker
                }

                val finalDestinationMaker = LatLng(it.finalDestination.latitude, it.finalDestination.longitude)

                Marker(
                    state = riderState,
                    title = "Rider",
                    snippet = "Rider",
                    icon = bitmapDescriptorFromVector(
                        context,
                        com.amos_tech_code.foodhub.R.drawable.ic_delivery
                    )
                )

                Marker(
                    state = rememberMarkerState(position = finalDestinationMaker),
                    title = "Customer",
                    snippet = "Customer",
                    icon = bitmapDescriptorFromVector(
                        context,
                        com.amos_tech_code.foodhub.R.drawable.ic_home
                    )

                )

                Polyline(
                    points = it.polyline,
                    color = MaterialTheme.colorScheme.primary,
                    width = 8f
                )
            }
        }

    }


}


fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorResId: Int
): BitmapDescriptor {

    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable!!.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}