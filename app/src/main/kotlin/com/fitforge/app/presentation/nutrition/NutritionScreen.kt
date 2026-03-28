package com.fitforge.app.presentation.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitforge.app.data.local.db.entity.MealEntity
import com.fitforge.app.domain.usecase.nutrition.DailyNutritionSummary
import com.fitforge.app.presentation.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    uiState: NutritionUiState,
    onAddMeal: () -> Unit,
    onDeleteMeal: (String) -> Unit,
    onDismissError: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onDismissError()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nutrition") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMeal) {
                Icon(Icons.Default.Add, contentDescription = "Add Meal")
            }
        },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("nutrition_loading"))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.screenHorizontalPadding, vertical = Spacing.screenTopPadding)
                .testTag("nutrition_screen"),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // Nutrition Summary Card
            item {
                NutritionSummaryCard(summary = uiState.summary)
            }

            // Macros Breakdown
            item {
                MacrosCard(summary = uiState.summary)
            }

            // Meals Section Header
            item {
                Text(
                    text = "Today's Meals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = Spacing.sm),
                )
            }

            // Meals List
            if (uiState.meals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No meals logged today",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                items(uiState.meals, key = { it.id }) { meal ->
                    MealCard(
                        meal = meal,
                        onDelete = { onDeleteMeal(meal.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NutritionSummaryCard(summary: DailyNutritionSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("nutrition_summary_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Daily Calories",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${summary.totalCalories} / ${summary.calorieGoal}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { min(summary.totalCalories.toFloat() / summary.calorieGoal, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            val remaining = summary.calorieGoal - summary.totalCalories
            Text(
                text = if (remaining > 0) "$remaining kcal remaining" else "${-remaining} kcal over",
                style = MaterialTheme.typography.bodyMedium,
                color = if (remaining > 0) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
        }
    }
}

@Composable
private fun MacrosCard(summary: DailyNutritionSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("macros_card"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
        ) {
            Text(
                text = "Macros",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MacroItem(
                    name = "Protein",
                    current = summary.totalProtein,
                    goal = summary.proteinGoal,
                    unit = "g",
                )
                MacroItem(
                    name = "Carbs",
                    current = summary.totalCarbs,
                    goal = summary.carbsGoal,
                    unit = "g",
                )
                MacroItem(
                    name = "Fat",
                    current = summary.totalFat,
                    goal = summary.fatGoal,
                    unit = "g",
                )
            }
        }
    }
}

@Composable
private fun MacroItem(
    name: String,
    current: Float,
    goal: Float,
    unit: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${current.toInt()}${unit}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "/ ${goal.toInt()}${unit}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { min(current / goal, 1f) },
            modifier = Modifier
                .size(60.dp, 4.dp),
        )
    }
}

@Composable
private fun MealCard(
    meal: MealEntity,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("meal_card_${meal.id}"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = meal.mealType.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = meal.mealName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        Text(
                            text = timeFormat.format(Date(meal.mealTimeEpochMillis)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "${meal.totalCalories} kcal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Text(
                        text = "P: ${meal.totalProteinGrams.toInt()}g",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "C: ${meal.totalCarbsGrams.toInt()}g",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "F: ${meal.totalFatGrams.toInt()}g",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete meal",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
