package dev.dericbourg.firstclassmetronome.presentation.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class AppScreen : Parcelable {
    @Parcelize
    data object BeatSelection : AppScreen()
    @Parcelize
    data object WorkLog : AppScreen()
    @Parcelize
    data object Settings : AppScreen()
}
