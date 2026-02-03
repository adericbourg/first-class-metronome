package dev.dericbourg.firstclassmetronone

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import dev.dericbourg.firstclassmetronone.lifecycle.AppLifecycleObserver
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
