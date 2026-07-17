package dev.dericbourg.firstclassmetronome.device

import android.os.Build
import android.os.Vibrator
import javax.inject.Inject

interface DeviceCapabilities {
    val hasVibrator: Boolean
    val hasAmplitudeControl: Boolean
    val supportsDynamicColors: Boolean
}

class DefaultDeviceCapabilities @Inject constructor(
    private val vibrator: Vibrator
) : DeviceCapabilities {

    override val hasVibrator: Boolean
        get() = vibrator.hasVibrator()

    override val hasAmplitudeControl: Boolean
        get() = vibrator.hasAmplitudeControl()

    override val supportsDynamicColors: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}
