package com.example.kinetic.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "profile_settings",
    foreignKeys = [ForeignKey(
        entity = Profile::class,
        parentColumns = ["id"],
        childColumns = ["profileId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProfileSettings(
    @PrimaryKey val profileId: Long,
    val isDarkModeEnabled: Boolean = false,
    val selectedAccent: String = "Pulse",
    val showWorkoutTutorials: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val isHighContrastEnabled: Boolean = false,
    val isCardLayout: Boolean = true
)
