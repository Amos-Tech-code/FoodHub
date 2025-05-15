package com.amos_tech_code.foodhub.ui.feature.order_failure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OrderFailed() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Order Failed",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "We are sorry for the inconvenience. Please try again later",
            style = MaterialTheme.typography.bodyMedium
        )

    }
}