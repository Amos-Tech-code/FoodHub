package com.amos_tech_code.foodhub.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amos_tech_code.foodhub.data.FoodApi
import com.amos_tech_code.foodhub.data.model.request.FCMTokenRequest
import com.amos_tech_code.foodhub.data.remote.ApiResponse
import com.amos_tech_code.foodhub.data.remote.safeApiCall
import com.amos_tech_code.foodhub.R
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.internal.http2.Settings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodHubNotificationManager @Inject constructor(
    private val foodApi: FoodApi,
    @ApplicationContext private val context: Context,
) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val job = CoroutineScope(Dispatchers.IO + SupervisorJob() )

    enum class NotificationChannelType(
        val id: String,
        val channelName: String,
        val channelDescription: String,
        val importance: Int
    ) {
        ORDER("1", "Order", "Order", NotificationManager.IMPORTANCE_HIGH),
        PROMOTION("2", "Promotion", "Promotion", NotificationManager.IMPORTANCE_DEFAULT),
        ACCOUNT("3", "Account", "Account", NotificationManager.IMPORTANCE_LOW)

    }

    fun createChannels() {

        NotificationChannelType.entries.forEach {
            val channel = NotificationChannelCompat.Builder(it.id, it.importance)
                .setDescription(it.channelDescription)
                .setName(it.channelName)
                .setVibrationEnabled(true)
                .setVibrationPattern(longArrayOf(100, 200, 100, 200))
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
                    android.app.Notification.AUDIO_ATTRIBUTES_DEFAULT)
                .build()
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun getAndStoreToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                updateFCMToken(it.result)
                Log.d("FCM_TOKEN", it.result)

            } else {
                Log.d("FCM_TOKEN", "Failed to get token $it.exception.toString()")
            }
        }
    }


    fun updateFCMToken(token: String) {
        job.launch {
            val res = safeApiCall(
                retryCount = 3,
                retryDelayMillis = 1000L,
                apiCall = { foodApi.updateFCMToken(FCMTokenRequest(token)) }
            )
            if(res is  ApiResponse.Success){
                Log.d("FCM_REQUEST", "${res.data.message}")
            }else{
                Log.d("FCM_REQUEST", "FAILED ${res}")
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(
        title: String,
        message: String,
        notificationID: Int,
        intent: PendingIntent,
        notificationChannelType: NotificationChannelType
    ) {

        val notification = NotificationCompat.Builder(context, notificationChannelType.id)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification_add)
            .setAutoCancel(true)
            .setContentIntent(intent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVibrate(longArrayOf(100, 200, 100, 200))
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(notificationID, notification)
    }

}