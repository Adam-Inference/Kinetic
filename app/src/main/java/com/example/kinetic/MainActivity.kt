package com.example.kinetic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.kinetic.data.Exercise
import com.example.kinetic.data.Profile
import com.example.kinetic.data.ProfileSettings
import com.example.kinetic.ui.screens.ProfileListScreen
import com.example.kinetic.ui.screens.SettingsScreen
import com.example.kinetic.ui.screens.SettingsState
import com.example.kinetic.ui.screens.WorkoutsListScreen
import com.example.kinetic.ui.screens.defaultWorkoutListState
import com.example.kinetic.ui.theme.AestheticVariant
import com.example.kinetic.ui.theme.KineticAccent
import com.example.kinetic.ui.theme.KineticAestheticTheme
import com.example.kinetic.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Screen {
    object Splash : Screen()
    object ProfileSelection : Screen()
    data class Workouts(val profile: Profile) : Screen()
    data class Settings(val profile: Profile) : Screen()
}

class MainActivity : ComponentActivity() {

    private val vm: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val profiles      by vm.profiles.collectAsState()
            val isInitialized by vm.isInitialized.collectAsState()
            val settings      by vm.currentSettings.collectAsState()
            val exercises     by vm.currentExercises.collectAsState()

            var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
            var splashDone    by remember { mutableStateOf(false) }

            // Minimum splash display time
            LaunchedEffect(Unit) {
                delay(1100)
                splashDone = true
            }

            // Auto-navigate once both splash is done and data is ready
            LaunchedEffect(splashDone, isInitialized) {
                if (splashDone && isInitialized && currentScreen is Screen.Splash) {
                    when {
                        profiles.size == 1 -> {
                            vm.selectProfile(profiles.first())
                            currentScreen = Screen.Workouts(profiles.first())
                        }
                        else -> currentScreen = Screen.ProfileSelection
                    }
                }
            }

            val themeVariant = if (settings?.isDarkModeEnabled == true)
                AestheticVariant.Dark else AestheticVariant.Light

            val accent = when (settings?.selectedAccent) {
                "Ember"  -> KineticAccent.Ember
                "Cosmic" -> KineticAccent.Cosmic
                else     -> KineticAccent.Pulse
            }

            val highContrast = settings?.isHighContrastEnabled == true

            KineticAestheticTheme(
                variant      = themeVariant,
                accent       = accent,
                highContrast = highContrast
            ) {
                // Back handler: from Settings → Workouts; block back from Splash/Workouts/ProfileSelection
                BackHandler(
                    enabled = currentScreen is Screen.Settings
                ) {
                    val s = currentScreen
                    if (s is Screen.Settings) currentScreen = Screen.Workouts(s.profile)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (val screen = currentScreen) {

                        is Screen.Splash -> {
                            SplashScreen(modifier = Modifier.padding(innerPadding))
                        }

                        is Screen.ProfileSelection -> {
                            ProfileListScreen(
                                profiles        = profiles,
                                onProfileSelect = { profile ->
                                    vm.selectProfile(profile)
                                    currentScreen = Screen.Workouts(profile)
                                },
                                onAddProfile    = { name -> vm.addProfile(name) },
                                modifier        = Modifier.padding(innerPadding)
                            )
                        }

                        is Screen.Workouts -> {
                            WorkoutsListScreen(
                                state            = defaultWorkoutListState,
                                exercises        = exercises,
                                profileId        = screen.profile.id,
                                profileName      = screen.profile.name,
                                isCardLayout     = settings?.isCardLayout ?: true,
                                onExerciseUpdate = { exercise -> vm.saveExercise(exercise) },
                                onSettingsClick  = { currentScreen = Screen.Settings(screen.profile) },
                                modifier         = Modifier.padding(innerPadding)
                            )
                        }

                        is Screen.Settings -> {
                            val profileSettings = settings
                                ?: ProfileSettings(profileId = screen.profile.id)

                            SettingsScreen(
                                state = SettingsState(
                                    isDarkModeEnabled    = profileSettings.isDarkModeEnabled,
                                    isHighContrastEnabled = profileSettings.isHighContrastEnabled,
                                    isCardLayout         = profileSettings.isCardLayout,
                                    selectedAccent       = when (profileSettings.selectedAccent) {
                                        "Ember"  -> KineticAccent.Ember
                                        "Cosmic" -> KineticAccent.Cosmic
                                        else     -> KineticAccent.Pulse
                                    },
                                    currentProfile = screen.profile,
                                    allProfiles    = profiles
                                ),
                                onDarkModeToggle     = { enabled ->
                                    vm.updateSettings(profileSettings.copy(isDarkModeEnabled = enabled))
                                },
                                onHighContrastToggle = { enabled ->
                                    vm.updateSettings(profileSettings.copy(isHighContrastEnabled = enabled))
                                },
                                onCardLayoutToggle   = { isCard ->
                                    vm.updateSettings(profileSettings.copy(isCardLayout = isCard))
                                },
                                onAccentSelected     = { selectedAccent ->
                                    vm.updateSettings(profileSettings.copy(selectedAccent = selectedAccent.name))
                                },
                                onExportCsv          = { ctx ->
                                    exportWorkoutsCsv(
                                        context   = ctx,
                                        profile   = screen.profile,
                                        exercises = exercises,
                                        workouts  = defaultWorkoutListState.workouts
                                            .map { Triple(it.id, it.title, it.category) }
                                    )
                                },
                                onAddProfile         = { name -> vm.addProfile(name) },
                                onRenameProfile      = { profile, newName ->
                                    vm.renameProfile(profile, newName)
                                    if (profile.id == screen.profile.id) {
                                        val updated = profile.copy(name = newName)
                                        vm.selectProfile(updated)
                                        currentScreen = Screen.Settings(updated)
                                    }
                                },
                                onDeleteProfile      = { profile ->
                                    if (profiles.size > 1) {
                                        val next = profiles.first { it.id != profile.id }
                                        vm.deleteProfile(profile)
                                        vm.selectProfile(next)
                                        currentScreen = Screen.Workouts(next)
                                    }
                                },
                                onSwitchProfile      = { profile ->
                                    vm.selectProfile(profile)
                                    currentScreen = Screen.Workouts(profile)
                                },
                                onBackClick          = {
                                    currentScreen = Screen.Workouts(screen.profile)
                                },
                                modifier             = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashScreen(modifier: Modifier = Modifier) {
    var animVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(80)
        animVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            AnimatedVisibility(
                visible = animVisible,
                enter   = fadeIn(tween(380)) + scaleIn(tween(380), initialScale = 0.72f)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "K",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            AnimatedVisibility(
                visible = animVisible,
                enter   = fadeIn(tween(400, delayMillis = 120))
            ) {
                Text(
                    text  = "KINETIC",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight    = FontWeight.Black,
                        letterSpacing = 6.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = animVisible,
                enter   = fadeIn(tween(400, delayMillis = 220))
            ) {
                Text(
                    text  = "Track. Progress. Dominate.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                )
            }
        }
    }
}

private fun exportWorkoutsCsv(
    context:   Context,
    profile:   Profile,
    exercises: Map<String, Exercise>,
    workouts:  List<Triple<String, String, String>>   // id, title, category
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val safeProfileName = profile.name
        .replace(" ", "_")
        .replace(Regex("[^A-Za-z0-9_]"), "")
        .take(40)
        .ifEmpty { "Profile" }
    val filename = "WorkoutHistory_${safeProfileName}_${today}.csv"

    val csv = buildString {
        appendLine("WorkoutName,Weight,Unit,DateSaved")
        workouts.forEach { (id, title, _) ->
            val ex = exercises[id] ?: return@forEach
            if (ex.weight <= 0.0) return@forEach
            val name = "\"${title.replace("\"", "\"\"")}\""
            val weight = if (ex.weight == kotlin.math.floor(ex.weight))
                ex.weight.toLong().toString()
            else
                ex.weight.toString()
            appendLine("$name,$weight,${ex.weightUnit},$today")
        }
    }

    try {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: context.filesDir
        dir.mkdirs()
        val file = File(dir, filename)
        file.writeText(csv)

        val uri = FileProvider.getUriForFile(
            context,
            "com.example.kinetic.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Kinetic Workout History — $today")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Save $filename"))
    } catch (e: Exception) {
        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
