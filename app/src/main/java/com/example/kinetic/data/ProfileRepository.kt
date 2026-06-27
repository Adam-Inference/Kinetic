package com.example.kinetic.data

import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val dao: ProfileDao) {

    val allProfiles: Flow<List<Profile>> = dao.getAllProfiles()

    fun getSettings(profileId: Long): Flow<ProfileSettings?> =
        dao.getProfileSettings(profileId)

    fun getExercises(profileId: Long): Flow<List<Exercise>> =
        dao.getExercisesForProfile(profileId)

    suspend fun addProfile(name: String): Long =
        dao.insertProfile(Profile(name = name.trim()))

    suspend fun renameProfile(profile: Profile, newName: String) =
        dao.updateProfile(profile.copy(name = newName.trim()))

    suspend fun deleteProfile(profile: Profile) =
        dao.deleteProfile(profile)

    suspend fun saveSettings(settings: ProfileSettings) =
        dao.saveSettings(settings)

    suspend fun saveExercise(exercise: Exercise) =
        dao.saveExercise(exercise)
}
