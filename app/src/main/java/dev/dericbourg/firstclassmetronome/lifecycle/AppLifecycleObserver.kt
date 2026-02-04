package dev.dericbourg.firstclassmetronome.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.dericbourg.firstclassmetronome.data.repository.PracticeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor(
    private val practiceRepository: PracticeRepository
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(owner: LifecycleOwner) {
        scope.launch {
            practiceRepository.fixUnterminatedSession()
            practiceRepository.compactOldEvents()
        }
    }
}
