package dev.dericbourg.firstclassmetronome.presentation.worklog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.dericbourg.firstclassmetronome.domain.model.PracticeStats

@Composable
fun StatsCard(
    stats: PracticeStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .semantics { contentDescription = "Practice statistics" },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            StatsSection(
                title = "Practice",
                values = listOf(
                    stats.last7Days.durationFormatted,
                    stats.last30Days.durationFormatted,
                    stats.allTime.durationFormatted
                ),
                subValues = listOf(
                    "(${stats.last7Days.daysWithPractice} days)",
                    "(${stats.last30Days.daysWithPractice} days)",
                    "(${stats.allTime.daysWithPractice} days)"
                )
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            StatsSection(
                title = "Average per session",
                values = listOf(
                    stats.last7Days.avgPerSessionFormatted,
                    stats.last30Days.avgPerSessionFormatted,
                    stats.allTime.avgPerSessionFormatted
                )
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            StatsSection(
                title = "Average per day",
                values = listOf(
                    stats.last7Days.avgPerDayFormatted,
                    stats.last30Days.avgPerDayFormatted,
                    stats.allTime.avgPerDayFormatted
                )
            )
        }
    }
}

@Composable
private fun StatsSection(
    title: String,
    values: List<String>,
    subValues: List<String>? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.semantics { heading() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatLabel("7 days", Modifier.weight(1f))
            StatLabel("30 days", Modifier.weight(1f))
            StatLabel("All time", Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            values.forEachIndexed { index, value ->
                val period = when (index) {
                    0 -> "7 days"
                    1 -> "30 days"
                    else -> "All time"
                }
                StatValue(
                    label = title,
                    period = period,
                    value = value,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (subValues != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                subValues.forEach { subValue ->
                    Text(
                        text = subValue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun StatLabel(text: String, modifier: Modifier = Modifier) {
    val displayText = text.split(" ").first() + "d"
    Text(
        text = displayText,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.semantics {
            contentDescription = text
        },
        textAlign = TextAlign.Center
    )
}

@Composable
private fun StatValue(
    label: String,
    period: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.semantics {
            contentDescription = "$label for $period: $value"
        },
        textAlign = TextAlign.Center
    )
}
