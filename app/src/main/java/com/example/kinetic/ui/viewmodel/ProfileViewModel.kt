package com.example.kinetic.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kinetic.data.AppDatabase
import com.example.kinetic.data.Profile
import com.example.kinetic.data.ProfileRepository
import com.example.kinetic.data.ProfileSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ProfileRepository(AppDatabase.getInstance(application).profileDao())

    val profiles: StateFlow<List<Profile>> = repo.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedProfile = MutableStateFlow<Profile?>(null)
    val selectedProfile: StateFlow<Profile?> = _selectedProfile.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentSettings: StateFlow<ProfileSettings?> = _selectedProfile
        .flatMapLatest { profile ->
            if (profile != null) repo.getSettings(profile.id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun selectProfile(profile: Profile) {
        _selectedProfile.value = profile
    }

    fun clearSelectedProfile() {
        _selectedProfile.value = null
    }

    fun addProfile(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repo.addProfile(name) }
    }

    fun renameProfile(profile: Profile, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch { repo.renameProfile(profile, newName) }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            repo.deleteProfile(profile)
            if (_selectedProfile.value?.id == profile.id) {
                _selectedProfile.value = null
            }
        }
    }

    fun updateSettings(settings: ProfileSettings) {
        viewModelScope.launch { repo.saveSettings(settings) }
    }
}
