package dev.dericbourg.firstclassmetronome

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import dev.dericbourg.firstclassmetronome.lifecycle.AppLifecycleObserver
import javax.inject.Inject

@HiltAndroidApp
class MetronomeApplication : Application() {

    @Inject
    lateinit var appLifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
    }
}
