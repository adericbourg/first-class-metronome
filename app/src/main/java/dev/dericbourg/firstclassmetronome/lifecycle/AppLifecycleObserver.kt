package dev.dericbourg.firstclassmetronome.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.dericbourg.firstclassmetronome.data.repository.PracticeRepository
import dev.dericbourg.firstclassmetronome.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor(
    private val practiceRepository: PracticeRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    override fun onCreate(owner: LifecycleOwner) {
        scope.launch {
            practiceRepository.fixUnterminatedSession()
            practiceRepository.compactOldEvents()
        }
    }
}
