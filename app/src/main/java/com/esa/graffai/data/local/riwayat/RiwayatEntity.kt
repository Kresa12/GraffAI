package com.esa.graffai.data.local.riwayat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riwayat")
data class RiwayatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUri: String,
    val predictionLabel: String,
    val confidence: Float
)
