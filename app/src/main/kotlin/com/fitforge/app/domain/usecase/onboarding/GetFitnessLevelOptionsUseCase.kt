package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.data.local.datastore.UserPrefs
import javax.inject.Inject

data class FitnessLevelOption(
    val storageValue: String,
    val title: String,
    val description: String,
)

class GetFitnessLevelOptionsUseCase @Inject constructor() {
    operator fun invoke(): List<FitnessLevelOption> = listOf(
        FitnessLevelOption(
            storageValue = UserPrefs.FitnessLevel.BEGINNER,
            title = "Beginner",
            description = "You are building consistency and want guided fundamentals with lower training volume.",
        ),
        FitnessLevelOption(
            storageValue = UserPrefs.FitnessLevel.INTERMEDIATE,
            title = "Intermediate",
            description = "You train regularly and can handle balanced progressions across strength, cardio, and recovery.",
        ),
        FitnessLevelOption(
            storageValue = UserPrefs.FitnessLevel.ADVANCED,
            title = "Advanced",
            description = "You already manage structured training and want higher intensity, volume, and tighter progression.",
        ),
    )
}
