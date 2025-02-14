package com.amos_tech_code.foodhub

import android.app.Application
import android.content.pm.PackageManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()

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
