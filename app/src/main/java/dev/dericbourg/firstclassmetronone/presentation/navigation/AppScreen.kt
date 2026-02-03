package dev.dericbourg.firstclassmetronone.presentation.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class AppScreen : Parcelable {
    @Parcelize
    data object BeatSelection : AppScreen()
    @Parcelize
    data object WorkLog : AppScreen()
}
