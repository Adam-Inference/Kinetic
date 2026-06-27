package com.example.kinetic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AestheticVariant { Light, Dark }

enum class KineticAccent { Pulse, Ember, Cosmic }

private fun buildColorScheme(
    variant: AestheticVariant,
    accent: KineticAccent,
    highContrast: Boolean
): ColorScheme {
    val isDark = variant == AestheticVariant.Dark

    val primary = when (accent) {
        KineticAccent.Pulse  -> if (isDark) PulsePrimary       else PulsePrimaryLight
        KineticAccent.Ember  -> if (isDark) EmberPrimary       else EmberPrimaryLight
        KineticAccent.Cosmic -> if (isDark) CosmicPrimary      else CosmicPrimaryLight
    }
    val secondary = when (accent) {
        KineticAccent.Pulse  -> if (isDark) Color(0xFF9900CC)  else Color(0xFF6B009E)
        KineticAccent.Ember  -> if (isDark) Color(0xFFFF9060)  else Color(0xFFBF3500)
        KineticAccent.Cosmic -> if (isDark) Color(0xFF48C6EF)  else Color(0xFF4A90E2)
    }
    val tertiary = when (accent) {
        KineticAccent.Pulse  -> if (isDark) Color(0xFFDD55FF)  else Color(0xFFAA00EE)
        KineticAccent.Ember  -> if (isDark) Color(0xFFFFAA80)  else Color(0xFFFF6030)
        KineticAccent.Cosmic -> if (isDark) Color(0xFF8C65FF)  else Color(0xFF7B50FF)
    }

    return if (isDark) {
        val bg     = if (highContrast) Color.Black         else KineticDarkBackground
        val surf   = if (highContrast) Color(0xFF080808)   else KineticDarkSurface
        val onBg   = if (highContrast) Color.White         else KineticDarkOnBackground
        val onSurf = if (highContrast) Color.White         else KineticDarkOnSurface
        darkColorScheme(
            primary          = primary,
            onPrimary        = Color.White,
            secondary        = secondary,
            onSecondary      = Color.White,
            tertiary         = tertiary,
            onTertiary       = Color.White,
            background       = bg,
            onBackground     = onBg,
            surface          = surf,
            onSurface        = onSurf,
            surfaceVariant   = KineticDarkSurfaceVar,
            onSurfaceVariant = onSurf.copy(alpha = 0.72f),
            error            = KineticError,
            onError          = Color.White
        )
    } else {
        val bg     = if (highContrast) Color.White         else KineticLightBackground
        val surf   = if (highContrast) Color.White         else KineticLightSurface
        val onBg   = if (highContrast) Color.Black         else KineticLightOnBackground
        val onSurf = if (highContrast) Color.Black         else KineticLightOnSurface
        lightColorScheme(
            primary          = primary,
            onPrimary        = Color.White,
            secondary        = secondary,
            onSecondary      = Color.White,
            tertiary         = tertiary,
            onTertiary       = Color.White,
            background       = bg,
            onBackground     = onBg,
            surface          = surf,
            onSurface        = onSurf,
            surfaceVariant   = KineticLightSurfaceVar,
            onSurfaceVariant = onSurf.copy(alpha = 0.72f),
            error            = KineticError,
            onError          = Color.White
        )
    }
}

@Composable
fun KineticAestheticTheme(
    variant: AestheticVariant = if (isSystemInDarkTheme()) AestheticVariant.Dark else AestheticVariant.Light,
    accent: KineticAccent = KineticAccent.Pulse,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = buildColorScheme(variant, accent, highContrast),
        typography = Typography,
        content = content
    )
}
