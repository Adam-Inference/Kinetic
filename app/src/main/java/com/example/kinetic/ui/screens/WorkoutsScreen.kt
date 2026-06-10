package com.example.kinetic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WorkoutsListScreen(
    state: WorkoutListState,
    onWorkoutClick: (String) -> Unit,
    onWatchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Workouts",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Aesthetic routine overview",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                        )
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = state.workouts,
                    key = { it.id }
                ) { workout ->
                    val workoutClick = remember(workout.id, onWorkoutClick) {
                        { onWorkoutClick(workout.id) }
                    }
                    val watchClick = remember(workout.id, onWatchClick) {
                        { onWatchClick(workout.videoUrl) }
                    }

                    WorkoutListItem(
                        workout = workout,
                        onWorkoutClick = workoutClick,
                        onWatchClick = watchClick
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutListItem(
    workout: Workout,
    onWorkoutClick: () -> Unit,
    onWatchClick: () -> Unit
) {
    val formattedTitle = remember(workout.id, workout.title) {
        workout.title
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onWorkoutClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.98f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formattedTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = workout.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                    )
                }

                TextButton(onClick = onWatchClick) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Watch tutorial",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "Watch",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // TODO: add fixed YouTube link for this workout: ${workout.videoUrl}
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "Tap to review details and training cadence.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f)
                )
            }
        }
    }
}

@Composable
fun WorkoutsListAestheticDarkNoSetsPreview(onWorkoutClick: (String) -> Unit = {}, onWatchClick: (String) -> Unit = {}) {
    val state = defaultWorkoutListState
    com.example.kinetic.ui.theme.KineticAestheticTheme(variant = com.example.kinetic.ui.theme.AestheticVariant.Dark) {
        WorkoutsListScreen(
            state = state,
            onWorkoutClick = onWorkoutClick,
            onWatchClick = onWatchClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun WorkoutsListAestheticLightPreview(onWorkoutClick: (String) -> Unit = {}, onWatchClick: (String) -> Unit = {}) {
    val state = defaultWorkoutListState
    com.example.kinetic.ui.theme.KineticAestheticTheme(variant = com.example.kinetic.ui.theme.AestheticVariant.Light) {
        WorkoutsListScreen(
            state = state,
            onWorkoutClick = onWorkoutClick,
            onWatchClick = onWatchClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}
