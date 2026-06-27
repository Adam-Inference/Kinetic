package com.example.kinetic.ui.theme

import androidx.compose.ui.graphics.Color

// ── Kinetic Accent Primaries ───────────────────────────────────────────────

val PulsePrimary      = Color(0xFFBD00FF)   // Luminous Purple (dark)
val PulsePrimaryLight = Color(0xFF8A00BB)   // Accessible purple (light)

val EmberPrimary      = Color(0xFFFF6030)   // Energetic orange (dark)
val EmberPrimaryLight = Color(0xFFD04010)   // Deep orange (light)

val CosmicPrimary      = Color(0xFF6D77FF)  // Electric blue-violet (dark)
val CosmicPrimaryLight = Color(0xFF5C42FF)  // Deep indigo (light)

// ── Dark mode shared palette ───────────────────────────────────────────────

val KineticDarkBackground   = Color(0xFF131314)
val KineticDarkSurface      = Color(0xFF1E1E20)
val KineticDarkSurfaceVar   = Color(0xFF27272A)
val KineticDarkOnSurface    = Color(0xFFE8E8E8)
val KineticDarkOnBackground = Color(0xFFF0F0F0)

// ── Light mode shared palette ──────────────────────────────────────────────

val KineticLightBackground   = Color(0xFFF5F5F7)
val KineticLightSurface      = Color(0xFFFFFFFF)
val KineticLightSurfaceVar   = Color(0xFFF0F0F2)
val KineticLightOnSurface    = Color(0xFF1A1A1A)
val KineticLightOnBackground = Color(0xFF111111)

// ── Semantic ───────────────────────────────────────────────────────────────

val KineticError = Color(0xFFFF4D4D)

// ── Legacy aliases (kept to avoid compile errors in any remaining references)

val Purple80     = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80       = Color(0xFFEFB8C8)
val Purple40     = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40       = Color(0xFF7D5260)

val AestheticDarkPrimary      = CosmicPrimary
val AestheticDarkOnPrimary    = Color(0xFFFFFFFF)
val AestheticDarkSecondary    = Color(0xFF48C6EF)
val AestheticDarkOnSecondary  = Color(0xFF0D1219)
val AestheticDarkTertiary     = Color(0xFF8C65FF)
val AestheticDarkSurface      = KineticDarkSurface
val AestheticDarkOnSurface    = KineticDarkOnSurface
val AestheticDarkBackground   = KineticDarkBackground
val AestheticDarkOnBackground = KineticDarkOnBackground

val AestheticLightPrimary      = CosmicPrimaryLight
val AestheticLightOnPrimary    = Color(0xFFFFFFFF)
val AestheticLightSecondary    = Color(0xFF4A90E2)
val AestheticLightOnSecondary  = Color(0xFFFFFFFF)
val AestheticLightTertiary     = Color(0xFF8C65FF)
val AestheticLightSurface      = KineticLightSurface
val AestheticLightOnSurface    = KineticLightOnSurface
val AestheticLightBackground   = KineticLightBackground
val AestheticLightOnBackground = KineticLightOnBackground
