package com.scelio.brainest.presentation.util


import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

object DateTimeFormatter {

    fun formatDateTime(instant: Instant): String {
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val day = dateTime.day.toString().padStart(2, '0')
        val month = dateTime.month.number.toString().padStart(2, '0')
        val year = dateTime.year

        val hour12 = if (dateTime.hour == 0) 12 else if (dateTime.hour > 12) dateTime.hour - 12 else dateTime.hour
        val minute = dateTime.minute.toString().padStart(2, '0')
        val amPm = if (dateTime.hour < 12) "AM" else "PM"

        return "$day/$month/$year, $hour12:$minute $amPm"
    }

    fun formatTime(instant: Instant): String {
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val hour12 = if (dateTime.hour == 0) 12 else if (dateTime.hour > 12) dateTime.hour - 12 else dateTime.hour
        val minute = dateTime.minute.toString().padStart(2, '0')
        val amPm = if (dateTime.hour < 12) "AM" else "PM"

        return "$hour12:$minute $amPm"
    }

    fun formatDate(instant: Instant): String {
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val day = dateTime.day.toString().padStart(2, '0')
        val month = dateTime.month.number.toString().padStart(2, '0')
        val year = dateTime.year

        return "$day/$month/$year"
    }

    fun formatRelativeDateTime(instant: Instant): String {
        val timeZone = TimeZone.currentSystemDefault()
        val messageDate = instant.toLocalDateTime(timeZone).date
        val today = Clock.System.now().toLocalDateTime(timeZone).date

        val time = formatTime(instant)

        return when (messageDate) {
            today -> "Today, $time"
            today.minus(1, kotlinx.datetime.DateTimeUnit.DAY) -> "Yesterday, $time"
            else -> formatDateTime(instant)
        }
    }
}