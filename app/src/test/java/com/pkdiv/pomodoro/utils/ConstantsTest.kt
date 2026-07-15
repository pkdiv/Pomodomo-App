package com.pkdiv.pomodoro.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL

/**
 * Unit tests for [Constants]. These run on the JVM via `./gradlew test`.
 *
 * Keep pure-logic assertions here — no Android framework dependencies so the
 * suite stays fast and hermetic.
 */
class ConstantsTest {

    @Test
    fun `target url points at the hosted pomodoro app`() {
        assertEquals("https://apps.pkdiv.com/pomodoro", Constants.TARGET_URL)
    }

    @Test
    fun `allowed scheme is https only`() {
        assertEquals("https", Constants.ALLOWED_SCHEME)
    }

    @Test
    fun `target url matches the allowed host and scheme`() {
        val url = URL(Constants.TARGET_URL)
        assertEquals(Constants.ALLOWED_HOST, url.host)
        assertEquals(Constants.ALLOWED_SCHEME, url.protocol)
    }

    @Test
    fun `notification channel and timer id are defined`() {
        assertEquals("pomodoro_channel", Constants.NOTIFICATION_CHANNEL_ID)
        assertEquals(1001, Constants.NOTIFICATION_TIMER_DONE_ID)
        assertTrue(Constants.NOTIFICATION_TIMER_DONE_ID > 0)
    }

    @Test
    fun `file chooser request code is a positive distinct constant`() {
        assertTrue(Constants.FILE_CHOOSER_REQUEST_CODE > 0)
    }
}
