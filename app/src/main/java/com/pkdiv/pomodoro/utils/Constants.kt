package com.pkdiv.pomodoro.utils

/**
 * App-wide constants. The web app is the single source of truth — these values
 * pin the wrapper to the hosted Pomodoro instance and the trusted domain.
 */
object Constants {
    const val TARGET_URL = "https://apps.pkdiv.com/pomodoro"
    const val ALLOWED_HOST = "apps.pkdiv.com"
    const val ALLOWED_SCHEME = "https"

    const val NOTIFICATION_CHANNEL_ID = "pomodoro_channel"
    const val NOTIFICATION_TIMER_DONE_ID = 1001

    const val KEY_LAST_URL = "last_url"

    const val FILE_CHOOSER_REQUEST_CODE = 2001
}
