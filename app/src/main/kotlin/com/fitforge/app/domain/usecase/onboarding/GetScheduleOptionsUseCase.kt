package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.data.local.datastore.UserPrefs
import javax.inject.Inject

data class WorkoutDayOption(
    val storageValue: String,
    val shortLabel: String,
    val fullLabel: String,
)

class GetScheduleOptionsUseCase @Inject constructor() {
    operator fun invoke(): List<WorkoutDayOption> = listOf(
        WorkoutDayOption(UserPrefs.WorkoutDay.MONDAY, "M", "Monday"),
        WorkoutDayOption(UserPrefs.WorkoutDay.TUESDAY, "T", "Tuesday"),
        WorkoutDayOption(UserPrefs.WorkoutDay.WEDNESDAY, "W", "Wednesday"),
        WorkoutDayOption(UserPrefs.WorkoutDay.THURSDAY, "T", "Thursday"),
        WorkoutDayOption(UserPrefs.WorkoutDay.FRIDAY, "F", "Friday"),
        WorkoutDayOption(UserPrefs.WorkoutDay.SATURDAY, "S", "Saturday"),
        WorkoutDayOption(UserPrefs.WorkoutDay.SUNDAY, "S", "Sunday"),
    )
}
