package com.amos_tech_code.foodhub.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

interface NetworkMonitor {

    suspend fun isConnected(): Boolean

}


class NetworkMonitorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkMonitor {

    private fun isConnectedToNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun isConnected(): Boolean = withContext(Dispatchers.IO) {
        if (!isConnectedToNetwork()) return@withContext false

        try {
            val url = java.net.URL("http://clients3.google.com/generate_204")
            with(url.openConnection() as java.net.HttpURLConnection) {
                connectTimeout = 1500
                readTimeout = 1500
                connect()
                responseCode == 204
            }
        } catch (e: IOException) {
            false
        }
    }
}


object NetworkMonitorProvider {
    private var monitor: NetworkMonitor? = null

    fun init(monitor: NetworkMonitor) {
        this.monitor = monitor
    }

    fun get(): NetworkMonitor {
        return monitor ?: throw IllegalStateException("NetworkMonitor not initialized")
    }
}

