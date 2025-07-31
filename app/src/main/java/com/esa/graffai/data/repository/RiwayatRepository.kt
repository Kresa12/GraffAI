package com.esa.graffai.data.repository


import com.esa.graffai.data.local.riwayat.RiwayatDao
import com.esa.graffai.data.local.riwayat.RiwayatEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RiwayatRepository @Inject constructor(
    private val dao: RiwayatDao
) {
    fun getAllRiwayat(): Flow<List<RiwayatEntity>> = dao.getAllRiwayat()

    suspend fun insertRiwayat(riwayat: RiwayatEntity) = dao.insertRiwayat(riwayat)

    suspend fun deleteRiwayat(riwayat: RiwayatEntity) = dao.deleteRiwayat(riwayat)
}
