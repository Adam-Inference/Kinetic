package com.example.kinetic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.kinetic.data.Profile
import com.example.kinetic.data.ProfileSettings
import com.example.kinetic.ui.screens.ProfileListScreen
import com.example.kinetic.ui.screens.SettingsAccent
import com.example.kinetic.ui.screens.SettingsScreen
import com.example.kinetic.ui.screens.SettingsState
import com.example.kinetic.ui.screens.WorkoutsListScreen
import com.example.kinetic.ui.screens.defaultWorkoutListState
import com.example.kinetic.ui.theme.AestheticVariant
import com.example.kinetic.ui.theme.KineticAestheticTheme
import com.example.kinetic.ui.viewmodel.ProfileViewModel

sealed class Screen {
    object Profiles : Screen()
    data class Workouts(val profile: Profile) : Screen()
    data class Settings(val profile: Profile) : Screen()
}

class MainActivity : ComponentActivity() {

    private val vm: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val profiles by vm.profiles.collectAsState()
            val settings by vm.currentSettings.collectAsState()

            var currentScreen by remember { mutableStateOf<Screen>(Screen.Profiles) }

            val themeVariant = if (settings?.isDarkModeEnabled == true)
                AestheticVariant.Dark else AestheticVariant.Light

            KineticAestheticTheme(variant = themeVariant) {
                BackHandler(enabled = currentScreen !is Screen.Profiles) {
                    currentScreen = when (val s = currentScreen) {
                        is Screen.Settings -> Screen.Workouts(s.profile)
                        else -> {
                            vm.clearSelectedProfile()
                            Screen.Profiles
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (val screen = currentScreen) {

                        is Screen.Profiles -> {
                            ProfileListScreen(
                                profiles = profiles,
                                onProfileSelect = { profile ->
                                    vm.selectProfile(profile)
                                    currentScreen = Screen.Workouts(profile)
                                },
                                onAddProfile = { name -> vm.addProfile(name) },
                                onRenameProfile = { profile, newName ->
                                    vm.renameProfile(profile, newName)
                                },
                                onDeleteProfile = { profile -> vm.deleteProfile(profile) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is Screen.Workouts -> {
                            WorkoutsListScreen(
                                state = defaultWorkoutListState,
                                profileName = screen.profile.name,
                                onWorkoutClick = {},
                                onWatchClick = {},
                                onBackClick = {
                                    vm.clearSelectedProfile()
                                    currentScreen = Screen.Profiles
                                },
                                onSettingsClick = {
                                    currentScreen = Screen.Settings(screen.profile)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is Screen.Settings -> {
                            val profileSettings = settings
                                ?: ProfileSettings(profileId = screen.profile.id)

                            SettingsScreen(
                                state = SettingsState(
                                    isDarkModeEnabled = profileSettings.isDarkModeEnabled,
                                    selectedAccent = when (profileSettings.selectedAccent) {
                                        "Cosmic" -> SettingsAccent.Cosmic
                                        "Ember" -> SettingsAccent.Ember
                                        else -> SettingsAccent.Pulse
                                    },
                                    showWorkoutTutorials = profileSettings.showWorkoutTutorials,
                                    notificationsEnabled = profileSettings.notificationsEnabled
                                ),
                                onDarkModeToggle = { enabled ->
                                    vm.updateSettings(profileSettings.copy(isDarkModeEnabled = enabled))
                                },
                                onAccentSelected = { accent ->
                                    vm.updateSettings(profileSettings.copy(selectedAccent = accent.name))
                                },
                                onShowWorkoutTutorialsToggle = { enabled ->
                                    vm.updateSettings(profileSettings.copy(showWorkoutTutorials = enabled))
                                },
                                onNotificationsToggle = { enabled ->
                                    vm.updateSettings(profileSettings.copy(notificationsEnabled = enabled))
                                },
                                onBackClick = {
                                    currentScreen = Screen.Workouts(screen.profile)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
