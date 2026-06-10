package com.example.kinetic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AestheticLightColorScheme = lightColorScheme(
    primary = AestheticLightPrimary,
    onPrimary = AestheticLightOnPrimary,
    secondary = AestheticLightSecondary,
    onSecondary = AestheticLightOnSecondary,
    tertiary = AestheticLightTertiary,
    surface = AestheticLightSurface,
    onSurface = AestheticLightOnSurface,
    background = AestheticLightBackground,
    onBackground = AestheticLightOnBackground
)

private val AestheticDarkColorScheme = darkColorScheme(
    primary = AestheticDarkPrimary,
    onPrimary = AestheticDarkOnPrimary,
    secondary = AestheticDarkSecondary,
    onSecondary = AestheticDarkOnSecondary,
    tertiary = AestheticDarkTertiary,
    surface = AestheticDarkSurface,
    onSurface = AestheticDarkOnSurface,
    background = AestheticDarkBackground,
    onBackground = AestheticDarkOnBackground
)

enum class AestheticVariant {
    Light,
    Dark
}

@Composable
fun KineticAestheticTheme(
    variant: AestheticVariant = if (isSystemInDarkTheme()) AestheticVariant.Dark else AestheticVariant.Light,
    content: @Composable () -> Unit
) {
    val colorScheme = when (variant) {
        AestheticVariant.Light -> AestheticLightColorScheme
        AestheticVariant.Dark -> AestheticDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
