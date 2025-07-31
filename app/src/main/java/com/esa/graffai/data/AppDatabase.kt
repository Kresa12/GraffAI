package com.esa.graffai.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.esa.graffai.data.local.riwayat.RiwayatDao
import com.esa.graffai.data.local.riwayat.RiwayatEntity

@Database(entities = [RiwayatEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun riwayatDao() : RiwayatDao
}