package dev.dericbourg.firstclassmetronome.presentation.about

data class AboutState(
    val appName: String = "First-class Metronome",
    val author: String = "Alban Dericbourg",
    val authorEmail: String = "alban@dericbourg.dev",
    val sourceUrl: String = "https://github.com/adericbourg/FirstClassMetronome",
    val sourceDisplayLabel: String = "github.com/adericbourg/FirstClassMetronome",
    val licenseSummary: String = "This app's source code is licensed under the GNU " +
            "General Public License v3 — you're free to use, study, share, and modify it.",
    val versionDisplay: String = "",
    val systemInfo: String = ""
)
