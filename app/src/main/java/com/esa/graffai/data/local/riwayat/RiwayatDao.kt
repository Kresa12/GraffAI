package com.esa.graffai.data.local.riwayat

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RiwayatDao {

    @Query("SELECT * FROM riwayat")
    fun getAllRiwayat(): Flow<List<RiwayatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayat(riwayat: RiwayatEntity)

    @Delete
    suspend fun deleteRiwayat(riwayat: RiwayatEntity)
}