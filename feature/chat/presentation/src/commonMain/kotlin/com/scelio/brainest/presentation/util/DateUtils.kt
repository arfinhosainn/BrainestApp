package com.scelio.brainest.presentation.util

import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.today_x
import brainest.feature.chat.presentation.generated.resources.yesterday_x
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.Instant

object DateTimeFormatter {

    fun formatDateTime(instant: Instant): String {
        val dateTime = instant.toLocalDateTime(TimeZone.UTC)

        val day = dateTime.day.toString().padStart(2, '0')
        val month = dateTime.month.number.toString().padStart(2, '0')
        val year = dateTime.year

        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')

        return "$day/$month/$year, $hour:$minute"
    }

    fun formatTime(instant: Instant): String {
        val dateTime = instant.toLocalDateTime(TimeZone.UTC)

        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')

        return "$hour:$minute"
    }

    fun formatDate(instant: Instant): String {
        val dateTime = instant.toLocalDateTime(TimeZone.UTC)

        val day = dateTime.day.toString().padStart(2, '0')
        val month = dateTime.month.number.toString().padStart(2, '0')
        val year = dateTime.year

        return "$day/$month/$year"
    }

    suspend fun formatRelativeDateTime(instant: Instant): String {
        val timeZone = TimeZone.UTC
        val messageDate = instant.toLocalDateTime(timeZone).date
        val today = Clock.System.now().toLocalDateTime(timeZone).date

        val time = formatTime(instant)

        return when (messageDate) {
            today -> getString(Res.string.today_x, time)
            today.minus(1, kotlinx.datetime.DateTimeUnit.DAY) -> getString(Res.string.yesterday_x, time)
            else -> formatDateTime(instant)
        }
    }
}
