package com.example.kinetic.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kinetic.ui.theme.CosmicPrimary
import com.example.kinetic.ui.theme.EmberPrimary
import com.example.kinetic.ui.theme.KineticAccent
import com.example.kinetic.ui.theme.PulsePrimary

@Immutable
data class SettingsState(
    val isDarkModeEnabled: Boolean,
    val isHighContrastEnabled: Boolean,
    val isCardLayout: Boolean,
    val selectedAccent: KineticAccent
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onDarkModeToggle: (Boolean) -> Unit,
    onHighContrastToggle: (Boolean) -> Unit,
    onCardLayoutToggle: (Boolean) -> Unit,
    onAccentSelected: (KineticAccent) -> Unit,
    onBackClick: () -> Unit = {},
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
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    SettingsSectionLabel("Appearance")
                }

                item {
                    SettingsCard {
                        SettingsToggleRow(
                            label = "Dark mode",
                            description = "Switch between light and dark palette",
                            isChecked = state.isDarkModeEnabled,
                            onCheckedChange = onDarkModeToggle
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 0.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f)
                        )
                        SettingsToggleRow(
                            label = "High contrast",
                            description = "Maximum contrast for readability",
                            isChecked = state.isHighContrastEnabled,
                            onCheckedChange = onHighContrastToggle
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 0.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f)
                        )
                        SettingsToggleRow(
                            label = "Card layout",
                            description = "Cards view vs flat list view",
                            isChecked = state.isCardLayout,
                            onCheckedChange = onCardLayoutToggle
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    SettingsSectionLabel("Theme accent")
                }

                item {
                    SettingsCard {
                        Text(
                            text = "Choose your color accent. Each profile can have its own.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )
                        AccentPicker(
                            selected = state.selectedAccent,
                            onSelect = onAccentSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionLabel(label: String) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = MaterialTheme.shapes.large
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)
            )
        )
    }
}

@Composable
private fun AccentPicker(
    selected: KineticAccent,
    onSelect: (KineticAccent) -> Unit
) {
    val options = listOf(
        Triple(KineticAccent.Pulse,  PulsePrimary,  "Pulse"),
        Triple(KineticAccent.Ember,  EmberPrimary,  "Ember"),
        Triple(KineticAccent.Cosmic, CosmicPrimary, "Cosmic")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { (accent, color, name) ->
            AccentChip(
                name = name,
                color = color,
                isSelected = selected == accent,
                onClick = { onSelect(accent) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AccentChip(
    name: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) color else Color.Transparent,
        animationSpec = tween(200),
        label = "accentBorder"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) color.copy(alpha = 0.12f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "accentBg"
    )

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) borderColor
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) color
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}
