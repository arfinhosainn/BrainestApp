package com.scelio.brainest.home.domain

import kotlinx.datetime.LocalDate

/**
 * Represents the points schedule for a specific week.
 * Each day of the week has an assigned point value (2 or 8).
 */
data class WeeklyPointsSchedule(
    val userId: String,
    val weekStartDate: LocalDate,
    val mondayPoints: Int,
    val tuesdayPoints: Int,
    val wednesdayPoints: Int,
    val thursdayPoints: Int,
    val fridayPoints: Int,
    val saturdayPoints: Int,
    val sundayPoints: Int
) {
    /**
     * Get points for a specific day of week (1=Monday, 7=Sunday)
     */
    fun getPointsForDay(dayOfWeek: Int): Int {
        return when (dayOfWeek) {
            1 -> mondayPoints
            2 -> tuesdayPoints
            3 -> wednesdayPoints
            4 -> thursdayPoints
            5 -> fridayPoints
            6 -> saturdayPoints
            7 -> sundayPoints
            else -> 0
        }
    }
}
