package com.pkdiv.pomodoro.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pkdiv.pomodoro.R
import com.pkdiv.pomodoro.utils.Constants

/**
 * Native notification support for timer completion / reminders.
 *
 * Scheduling logic stays in the web app (the source of truth). This helper only
 * creates the channel and posts notifications — it does not reimplement timer
 * math. Permission is requested by [PermissionHelper] before posting on 13+.
 */
class NotificationHelper(private val context: Context) {

    private val manager = NotificationManagerCompat.from(context)

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_description)
            }
            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    fun notifyTimerComplete() {
        if (!manager.areNotificationsEnabled()) return
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.notification_timer_done_title))
            .setContentText(context.getString(R.string.notification_timer_done_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify(Constants.NOTIFICATION_TIMER_DONE_ID, notification)
    }
}
