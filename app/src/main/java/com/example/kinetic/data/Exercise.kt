package com.example.kinetic.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [ForeignKey(
        entity = Profile::class,
        parentColumns = ["id"],
        childColumns = ["profileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("profileId")]
)
data class Exercise(
    @PrimaryKey val id: String,
    val profileId: Long,
    val workoutId: String,
    val weight: Double = 0.0,
    val weightUnit: String = "KG"
)
