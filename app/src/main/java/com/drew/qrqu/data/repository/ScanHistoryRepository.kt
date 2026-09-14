package com.drew.qrqu.data.repository

import com.drew.qrqu.data.ScanHistoryDao
import com.drew.qrqu.data.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

interface ScanHistoryRepository {
    fun getAllHistory(): Flow<List<ScanHistoryEntity>>
    suspend fun insertHistory(history: ScanHistoryEntity)
    suspend fun deleteHistory(history: ScanHistoryEntity)
}

class OfflineScanHistoryRepository(
    private val scanHistoryDao: ScanHistoryDao
) : ScanHistoryRepository {

    override fun getAllHistory(): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.getAllHistory()
    }

    override suspend fun insertHistory(history: ScanHistoryEntity) {
        scanHistoryDao.insertHistory(history)
    }

    override suspend fun deleteHistory(history: ScanHistoryEntity) {
        scanHistoryDao.deleteHistory(history)
    }
}

