package com.fitforge.app.presentation.onboarding.schedulesetup

import com.fitforge.app.domain.usecase.onboarding.WorkoutDayOption

data class ScheduleSetupUiState(
    val dayOptions: List<WorkoutDayOption> = emptyList(),
    val selectedDays: Set<String> = emptySet(),
    val durationMinutes: Int = 30,
    val dayError: String? = null,
    val destinationRoute: String? = null,
) {
    val canContinue: Boolean
        get() = selectedDays.isNotEmpty()
}
