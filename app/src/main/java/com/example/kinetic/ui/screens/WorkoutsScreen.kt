package com.example.kinetic.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kinetic.data.Exercise
import kotlinx.coroutines.delay

private fun formatWeight(weight: Double): String {
    if (weight <= 0.0) return ""
    return if (weight == kotlin.math.floor(weight)) weight.toLong().toString()
    else weight.toString()
}

private fun launchYouTube(context: android.content.Context, url: String) {
    if (url.isBlank()) {
        Toast.makeText(context, "No video linked to this exercise", Toast.LENGTH_SHORT).show()
        return
    }
    val uri = try { Uri.parse(url) } catch (e: Exception) {
        Toast.makeText(context, "Invalid video URL", Toast.LENGTH_SHORT).show()
        return
    }
    val youtubeIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.youtube")
    }
    try {
        context.startActivity(youtubeIntent)
    } catch (e: ActivityNotFoundException) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e2: Exception) {
            Toast.makeText(context, "Cannot open video", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsListScreen(
    state: WorkoutListState,
    exercises: Map<String, Exercise> = emptyMap(),
    profileId: Long = 0L,
    profileName: String = "",
    isCardLayout: Boolean = true,
    onExerciseUpdate: (Exercise) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(
                            text = "← Back",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = profileName.ifEmpty { "Workouts" },
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${state.workouts.size} exercises",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onSettingsClick) {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = if (isCardLayout) Arrangement.spacedBy(10.dp) else Arrangement.Top,
                contentPadding = if (isCardLayout)
                    androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                else
                    androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                items(
                    items = state.workouts,
                    key = { it.id }
                ) { workout ->
                    WorkoutRow(
                        workout = workout,
                        exercise = exercises[workout.id],
                        profileId = profileId,
                        isCardLayout = isCardLayout,
                        onExerciseUpdate = onExerciseUpdate
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutRow(
    workout: Workout,
    exercise: Exercise?,
    profileId: Long,
    isCardLayout: Boolean,
    onExerciseUpdate: (Exercise) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var initialized by remember { mutableStateOf(false) }
    var weightText by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("KG") }
    var unitExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(exercise?.id) {
        if (exercise != null) {
            weightText = formatWeight(exercise.weight)
            selectedUnit = exercise.weightUnit
        }
        delay(120L)
        initialized = true
    }

    LaunchedEffect(weightText, selectedUnit) {
        if (!initialized) return@LaunchedEffect
        delay(700L)
        val parsed = weightText.toDoubleOrNull() ?: 0.0
        onExerciseUpdate(
            Exercise(
                id = "${profileId}_${workout.id}",
                profileId = profileId,
                workoutId = workout.id,
                weight = parsed,
                weightUnit = selectedUnit
            )
        )
    }

    val accentColor = MaterialTheme.colorScheme.primary

    if (isCardLayout) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = accentColor.copy(alpha = 0.18f),
                    shape = MaterialTheme.shapes.large
                ),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            WorkoutContent(
                workout = workout,
                weightText = weightText,
                selectedUnit = selectedUnit,
                unitExpanded = unitExpanded,
                onWeightChange = { weightText = it },
                onUnitChange = { selectedUnit = it },
                onUnitExpandedChange = { unitExpanded = it },
                onWatchClick = { launchYouTube(context, workout.videoUrl) },
                onWeightDone = { focusManager.clearFocus() },
                modifier = Modifier.padding(14.dp)
            )
        }
    } else {
        Column {
            WorkoutContent(
                workout = workout,
                weightText = weightText,
                selectedUnit = selectedUnit,
                unitExpanded = unitExpanded,
                onWeightChange = { weightText = it },
                onUnitChange = { selectedUnit = it },
                onUnitExpandedChange = { unitExpanded = it },
                onWatchClick = { launchYouTube(context, workout.videoUrl) },
                onWeightDone = { focusManager.clearFocus() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutContent(
    workout: Workout,
    weightText: String,
    selectedUnit: String,
    unitExpanded: Boolean,
    onWeightChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onUnitExpandedChange: (Boolean) -> Unit,
    onWatchClick: () -> Unit,
    onWeightDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(primary.copy(alpha = 0.12f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = workout.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = primary,
                        fontSize = 10.sp
                    )
                }
            }

            TextButton(
                onClick = onWatchClick,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = "▶  Watch",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    ),
                    color = primary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = weightText,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() || it == '.' }
                    val dotCount = filtered.count { it == '.' }
                    if (dotCount <= 1 && filtered.length <= 8) onWeightChange(filtered)
                },
                placeholder = {
                    Text(
                        "Weight",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onWeightDone() }),
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f),
                    focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f),
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.small
            )

            ExposedDropdownMenuBox(
                expanded = unitExpanded,
                onExpandedChange = onUnitExpandedChange,
                modifier = Modifier.width(96.dp)
            ) {
                OutlinedTextField(
                    value = selectedUnit,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded)
                    },
                    modifier = Modifier.menuAnchor(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f),
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f),
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.small
                )
                ExposedDropdownMenu(
                    expanded = unitExpanded,
                    onDismissRequest = { onUnitExpandedChange(false) }
                ) {
                    listOf("KG", "LB").forEach { unit ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = unit,
                                    fontWeight = if (unit == selectedUnit) FontWeight.Bold else FontWeight.Normal,
                                    color = if (unit == selectedUnit)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onUnitChange(unit)
                                onUnitExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}
