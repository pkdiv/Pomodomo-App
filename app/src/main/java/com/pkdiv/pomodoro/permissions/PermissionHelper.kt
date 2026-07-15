package com.pkdiv.pomodoro.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/**
 * Wraps the runtime permission the wrapper actually needs: POST_NOTIFICATIONS
 * (Android 13+). Other permissions (camera/storage) are requested on demand by
 * the web app's file chooser flow, not up-front.
 */
class PermissionHelper(private val context: Context) {

    private val requestedNotifications =
        mutableSetOf<ActivityResultLauncher<String>>()

    fun needsNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
    }

    fun requestNotificationPermission(launcher: ActivityResultLauncher<String>) {
        if (!needsNotificationPermission()) return
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    companion object {
        const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
    }
}
