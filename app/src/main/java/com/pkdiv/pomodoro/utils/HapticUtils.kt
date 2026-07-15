package com.pkdiv.pomodoro.utils

import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Light haptic feedback helper. Calls are no-ops when the view is detached.
 */
fun View.performHaptic(constant: Int = HapticFeedbackConstants.VIRTUAL_KEY) {
    isHapticFeedbackEnabled = true
    performHapticFeedback(
        constant,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    )
}
