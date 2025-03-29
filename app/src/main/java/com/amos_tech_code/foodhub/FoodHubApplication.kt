package com.amos_tech_code.foodhub

import android.app.Application
import android.content.pm.PackageManager
import com.amos_tech_code.foodhub.notification.FoodHubNotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FoodHubApplication : Application() {

    @Inject
    lateinit var foodHubNotificationManager: FoodHubNotificationManager
    override fun onCreate() {
        super.onCreate()

        foodHubNotificationManager.createChannels()
        foodHubNotificationManager.getAndStoreToken()
        updateMetaData()
    }

    private fun updateMetaData() {
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = appInfo.metaData

            bundle.putString("com.facebook.sdk.ApplicationId", BuildConfig.FACEBOOK_APP_ID)
            bundle.putString("com.facebook.sdk.ClientToken", BuildConfig.FACEBOOK_CLIENT_TOKEN)
            bundle.putString("com.google.android.geo.API_KEY", BuildConfig.MAPS_API_KEY)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
