package dev.dericbourg.firstclassmetronome.presentation.worklog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.dericbourg.firstclassmetronome.domain.model.PracticeSession
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun SessionList(
    sessionsByDate: Map<LocalDate, List<PracticeSession>>,
    modifier: Modifier = Modifier
) {
    val sortedDates = sessionsByDate.keys.sortedDescending()
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sortedDates.forEach { date ->
            val sessions = sessionsByDate[date] ?: emptyList()

            item(key = "header-$date") {
                DateHeader(
                    date = date,
                    dateFormatter = dateFormatter,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(
                items = sessions,
                key = { session -> "session-${session.startTime}" }
            ) { session ->
                SessionCard(session = session)
            }
        }
    }
}

@Composable
private fun DateHeader(
    date: LocalDate,
    dateFormatter: DateTimeFormatter,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    val dateText = when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> date.format(dateFormatter)
    }

    Text(
        text = dateText,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
private fun SessionCard(
    session: PracticeSession,
    modifier: Modifier = Modifier
) {
    val accessibilityDescription = buildString {
        append("Practice session at ")
        append(session.startTimeFormatted)
        append(", duration ")
        append(session.durationFormatted)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = accessibilityDescription
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = session.startTimeFormatted,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = session.durationFormatted,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
