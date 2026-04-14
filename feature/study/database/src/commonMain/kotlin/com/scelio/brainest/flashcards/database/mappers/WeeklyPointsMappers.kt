package com.scelio.brainest.flashcards.database.mappers

import com.scelio.brainest.flashcards.database.entities.WeeklyPointsEntity
import com.scelio.brainest.home.domain.WeeklyPointsSchedule
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Instant

fun WeeklyPointsSchedule.toEntity(): WeeklyPointsEntity {
    return WeeklyPointsEntity(
        userId = userId,
        weekStartDate = weekStartDate.toString(),
        mondayPoints = mondayPoints,
        tuesdayPoints = tuesdayPoints,
        wednesdayPoints = wednesdayPoints,
        thursdayPoints = thursdayPoints,
        fridayPoints = fridayPoints,
        saturdayPoints = saturdayPoints,
        sundayPoints = sundayPoints,
        createdAt = Instant.fromEpochMilliseconds(0).toString(),
        updatedAt = Instant.fromEpochMilliseconds(0).toString()
    )
}

fun WeeklyPointsEntity.toDomain(): WeeklyPointsSchedule {
    return WeeklyPointsSchedule(
        userId = userId,
        weekStartDate = LocalDate.parse(weekStartDate),
        mondayPoints = mondayPoints,
        tuesdayPoints = tuesdayPoints,
        wednesdayPoints = wednesdayPoints,
        thursdayPoints = thursdayPoints,
        fridayPoints = fridayPoints,
        saturdayPoints = saturdayPoints,
        sundayPoints = sundayPoints
    )
}
