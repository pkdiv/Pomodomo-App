package com.pkdiv.pomodoro.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService

/**
 * Monitors connectivity and notifies via [onChange]. On recovery, the host can
 * reload the WebView automatically. Uses the modern
 * [ConnectivityManager.NetworkCallback] API (minSdk 24+). No coroutines needed
 * for this simple single-consumer signal.
 */
class NetworkMonitor(context: Context) {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()
        ?: error("ConnectivityManager unavailable")

    var onChange: ((isConnected: Boolean) -> Unit)? = null

    private val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = emit(true)
        override fun onLost(network: Network) = emit(false)

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val hasInternet =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            emit(hasInternet)
        }
    }

    fun register() {
        emit(isCurrentlyConnected())
        connectivityManager.registerNetworkCallback(request, callback)
    }

    fun unregister() {
        runCatching { connectivityManager.unregisterNetworkCallback(callback) }
    }

    fun isCurrentlyConnected(): Boolean {
        val active = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(active) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun emit(connected: Boolean) {
        onChange?.invoke(connected)
    }
}
