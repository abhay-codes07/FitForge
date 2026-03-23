package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.presentation.onboarding.goals.GoalOption
import javax.inject.Inject

class GetGoalOptionsUseCase @Inject constructor() {
    operator fun invoke(): List<GoalOption> {
        return listOf(
            GoalOption("weight_loss", "Weight loss", "Prioritize calorie burn, habit consistency, and sustainable progress."),
            GoalOption("muscle_gain", "Muscle gain", "Focus on progressive overload, recovery, and strength-building volume."),
            GoalOption("stay_fit", "Stay fit", "Balance cardio, mobility, and strength to stay generally healthy."),
            GoalOption("flexibility", "Flexibility", "Improve range of motion, posture, and recovery with mobility work."),
            GoalOption("endurance", "Endurance", "Build work capacity for longer sessions, runs, and conditioning blocks."),
        )
    }
}

