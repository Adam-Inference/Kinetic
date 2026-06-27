package com.example.kinetic.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Immutable
data class SettingsState(
    val isDarkModeEnabled: Boolean,
    val selectedAccent: SettingsAccent,
    val showWorkoutTutorials: Boolean,
    val notificationsEnabled: Boolean
)

enum class SettingsAccent {
    Cosmic,
    Pulse,
    Ember
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onDarkModeToggle: (Boolean) -> Unit,
    onAccentSelected: (SettingsAccent) -> Unit,
    onShowWorkoutTutorialsToggle: (Boolean) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accentLabel = remember(state.selectedAccent) {
        when (state.selectedAccent) {
            SettingsAccent.Cosmic -> "Cosmic"
            SettingsAccent.Pulse -> "Pulse"
            SettingsAccent.Ember -> "Ember"
        }
    }

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
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Fine-tune your Kinetic experience.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                }

                item {
                    SettingsRow(
                        label = "Dark mode",
                        description = "Switch between light and dark aesthetic palettes.",
                        isChecked = state.isDarkModeEnabled,
                        onCheckedChange = onDarkModeToggle
                    )
                }

                item {
                    SettingsRow(
                        label = "Workout tutorials",
                        description = "Enable quick access to guided workout videos.",
                        isChecked = state.showWorkoutTutorials,
                        onCheckedChange = onShowWorkoutTutorialsToggle
                    )
                }

                item {
                    SettingsRow(
                        label = "Notifications",
                        description = "Receive training reminders and progress tips.",
                        isChecked = state.notificationsEnabled,
                        onCheckedChange = onNotificationsToggle
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Theme accent",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Active: $accentLabel",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tap to change",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            ThemeOptions(state.selectedAccent, onAccentSelected)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeOptions(
    selectedAccent: SettingsAccent,
    onAccentSelected: (SettingsAccent) -> Unit
) {
    val options = listOf(SettingsAccent.Cosmic, SettingsAccent.Pulse, SettingsAccent.Ember)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { accent ->
            val isSelected = selectedAccent == accent
            SettingsRow(
                label = accent.name,
                description = if (isSelected) "Active accent" else "Tap to activate",
                isChecked = isSelected,
                onCheckedChange = { onAccentSelected(accent) }
            )
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        )
    }
}

@Composable
fun SettingsAestheticLightPreview(
    state: SettingsState = SettingsState(
        isDarkModeEnabled = false,
        selectedAccent = SettingsAccent.Pulse,
        showWorkoutTutorials = true,
        notificationsEnabled = true
    ),
    onDarkModeToggle: (Boolean) -> Unit = {},
    onAccentSelected: (SettingsAccent) -> Unit = {},
    onShowWorkoutTutorialsToggle: (Boolean) -> Unit = {},
    onNotificationsToggle: (Boolean) -> Unit = {}
) {
    com.example.kinetic.ui.theme.KineticAestheticTheme(variant = com.example.kinetic.ui.theme.AestheticVariant.Light) {
        SettingsScreen(
            state = state,
            onDarkModeToggle = onDarkModeToggle,
            onAccentSelected = onAccentSelected,
            onShowWorkoutTutorialsToggle = onShowWorkoutTutorialsToggle,
            onNotificationsToggle = onNotificationsToggle,
            modifier = Modifier.fillMaxSize()
        )
    }
}
