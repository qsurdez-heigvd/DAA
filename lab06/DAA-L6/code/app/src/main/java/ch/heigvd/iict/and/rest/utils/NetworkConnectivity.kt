// Authors: REDACTED, REDACTED, Quentin Surdez
package ch.heigvd.iict.and.rest.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import ch.heigvd.iict.and.rest.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Check if the device is connected to the internet.
 */
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

/**
 * Helper function that wraps a block of code with network availability check.
 */
suspend fun <T> withNetworkCheck(context: Context, block: suspend () -> T): T? {
    return if (isNetworkAvailable(context)) {
        block()
    } else {
        withContext(Dispatchers.Main) {
            Toast.makeText(context,
                context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
        }
        null
    }
}