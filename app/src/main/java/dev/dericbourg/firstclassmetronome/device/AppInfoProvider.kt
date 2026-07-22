package dev.dericbourg.firstclassmetronome.device

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface AppInfoProvider {
    val versionName: String
    val versionCode: Long
    val androidVersionRelease: String
    val androidApiLevel: Int
    val deviceModel: String
}

class DefaultAppInfoProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : AppInfoProvider {

    private val packageInfo by lazy {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }.getOrNull()
    }

    override val versionName: String
        get() = packageInfo?.versionName ?: "unknown"

    override val versionCode: Long
        get() = packageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                it.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                it.versionCode.toLong()
            }
        } ?: 0L

    override val androidVersionRelease: String
        get() = Build.VERSION.RELEASE

    override val androidApiLevel: Int
        get() = Build.VERSION.SDK_INT

    override val deviceModel: String
        get() = Build.MODEL
}
