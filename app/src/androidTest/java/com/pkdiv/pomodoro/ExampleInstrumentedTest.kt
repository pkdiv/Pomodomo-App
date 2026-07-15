package com.pkdiv.pomodoro

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test template. Runs on a device/emulator via
 * `./gradlew connectedAndroidTest`.
 *
 * Verify the app context resolves to the expected package so the wrapper is
 * wired up correctly. Add real UI/integration tests (Espresso) as needed —
 * the web app remains the source of truth, so most behavior lives there.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun `app context has the correct package name`() {
        val appContext = ApplicationProvider.getApplicationContext<android.content.Context>()
        assertEquals("com.pkdiv.pomodoro", appContext.packageName)
    }
}
