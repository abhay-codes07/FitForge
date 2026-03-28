package com.fitforge.app.domain.usecase.nutrition

import com.fitforge.app.data.local.db.entity.FoodItemEntity
import com.fitforge.app.data.local.db.entity.MealEntity
import com.fitforge.app.data.local.db.entity.NutritionGoalEntity
import com.fitforge.app.domain.repository.NutritionRepository
import com.fitforge.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import javax.inject.Inject

data class DailyNutritionSummary(
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float,
    val calorieGoal: Int,
    val proteinGoal: Float,
    val carbsGoal: Float,
    val fatGoal: Float,
)

class AddMealUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository,
) {
    suspend operator fun invoke(meal: MealEntity, foodItems: List<FoodItemEntity>): Result<Unit> {
        return nutritionRepository.addMeal(meal, foodItems)
    }
}

class GetMealsForTodayUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<MealEntity>> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(emptyList())
        val todayEpochDay = LocalDate.now().toEpochDay()
        return nutritionRepository.getMealsForDate(currentUser.id, todayEpochDay)
    }
}

class GetMealsForDateUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(dateEpochDay: Long): Flow<List<MealEntity>> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(emptyList())
        return nutritionRepository.getMealsForDate(currentUser.id, dateEpochDay)
    }
}

class DeleteMealUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository,
) {
    suspend operator fun invoke(mealId: String): Result<Unit> {
        return nutritionRepository.deleteMeal(mealId)
    }
}

class GetDailyNutritionSummaryUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(dateEpochDay: Long): Flow<DailyNutritionSummary> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(
            DailyNutritionSummary(0, 0f, 0f, 0f, 2000, 150f, 200f, 65f)
        )

        val caloriesFlow = nutritionRepository.getTotalCaloriesForDate(currentUser.id, dateEpochDay)
        val proteinFlow = nutritionRepository.getTotalProteinForDate(currentUser.id, dateEpochDay)
        val carbsFlow = nutritionRepository.getTotalCarbsForDate(currentUser.id, dateEpochDay)
        val fatFlow = nutritionRepository.getTotalFatForDate(currentUser.id, dateEpochDay)
        val goalFlow = nutritionRepository.getNutritionGoal(currentUser.id)

        return combine(caloriesFlow, proteinFlow, carbsFlow, fatFlow, goalFlow) {
            calories, protein, carbs, fat, goal ->
            DailyNutritionSummary(
                totalCalories = calories,
                totalProtein = protein,
                totalCarbs = carbs,
                totalFat = fat,
                calorieGoal = goal?.dailyCalorieGoal ?: 2000,
                proteinGoal = goal?.dailyProteinGoal ?: 150f,
                carbsGoal = goal?.dailyCarbsGoal ?: 200f,
                fatGoal = goal?.dailyFatGoal ?: 65f,
            )
        }
    }
}

class SetNutritionGoalUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        calorieGoal: Int,
        proteinGoal: Float,
        carbsGoal: Float,
        fatGoal: Float,
    ): Result<Unit> {
        val currentUser = userRepository.getPrimaryUser() ?: return Result.failure(
            Exception("User not logged in")
        )

        val goal = NutritionGoalEntity(
            userId = currentUser.id,
            dailyCalorieGoal = calorieGoal,
            dailyProteinGoal = proteinGoal,
            dailyCarbsGoal = carbsGoal,
            dailyFatGoal = fatGoal,
            updatedAtEpochMillis = System.currentTimeMillis(),
        )

        return nutritionRepository.setNutritionGoal(goal)
    }
}

class GetNutritionGoalUseCase @Inject constructor(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<NutritionGoalEntity?> {
        val currentUser = userRepository.getPrimaryUser() ?: return flowOf(null)
        return nutritionRepository.getNutritionGoal(currentUser.id)
    }
}
