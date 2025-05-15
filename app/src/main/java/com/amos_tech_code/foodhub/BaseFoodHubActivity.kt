package com.amos_tech_code.foodhub

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.amos_tech_code.foodhub.notification.FoodHubMessagingService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFoodHubActivity : ComponentActivity() {

    val viewModel by viewModels<MainViewModel> ()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(intent, viewModel)
    }

    protected fun processIntent(intent: Intent, viewModel: MainViewModel) {
        if (intent.hasExtra(FoodHubMessagingService.ORDER_ID)) {
            val orderID = intent.getStringExtra(FoodHubMessagingService.ORDER_ID)
            viewModel.navigateToOrderDetail(orderID!!)
            intent.removeExtra(FoodHubMessagingService.ORDER_ID)
        }
    }

}