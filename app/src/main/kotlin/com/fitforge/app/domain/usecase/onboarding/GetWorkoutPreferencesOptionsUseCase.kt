package com.fitforge.app.domain.usecase.onboarding

import com.fitforge.app.data.local.datastore.UserPrefs
import javax.inject.Inject

data class WorkoutLocationOption(
    val storageValue: String,
    val title: String,
    val description: String,
)

data class EquipmentOption(
    val storageValue: String,
    val title: String,
)

class GetWorkoutPreferencesOptionsUseCase @Inject constructor() {
    operator fun invoke(): Pair<List<WorkoutLocationOption>, List<EquipmentOption>> =
        buildList<WorkoutLocationOption> {
            add(
                WorkoutLocationOption(
                    storageValue = UserPrefs.WorkoutLocation.HOME,
                    title = "Home",
                    description = "Bodyweight sessions and minimal-equipment plans tuned for small spaces.",
                )
            )
            add(
                WorkoutLocationOption(
                    storageValue = UserPrefs.WorkoutLocation.GYM,
                    title = "Gym",
                    description = "Programs can assume access to machines, free weights, and heavier progressive overload.",
                )
            )
            add(
                WorkoutLocationOption(
                    storageValue = UserPrefs.WorkoutLocation.OUTDOOR,
                    title = "Outdoor",
                    description = "FitForge can bias toward runs, circuits, sprints, and mobility-friendly sessions outside.",
                )
            )
        } to buildList {
            add(EquipmentOption(UserPrefs.Equipment.NONE, "No equipment"))
            add(EquipmentOption(UserPrefs.Equipment.DUMBBELLS, "Dumbbells"))
            add(EquipmentOption(UserPrefs.Equipment.RESISTANCE_BANDS, "Bands"))
            add(EquipmentOption(UserPrefs.Equipment.KETTLEBELL, "Kettlebell"))
            add(EquipmentOption(UserPrefs.Equipment.BARBELL, "Barbell"))
            add(EquipmentOption(UserPrefs.Equipment.BENCH, "Bench"))
            add(EquipmentOption(UserPrefs.Equipment.TREADMILL, "Treadmill"))
            add(EquipmentOption(UserPrefs.Equipment.YOGA_MAT, "Yoga mat"))
        }
}
