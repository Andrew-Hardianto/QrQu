package com.drew.qrqu.di

import android.content.Context
import com.drew.qrqu.data.AppDatabase
import com.drew.qrqu.data.repository.OfflineScanHistoryRepository
import com.drew.qrqu.data.repository.ScanHistoryRepository
import com.drew.qrqu.domain.QrScannerHelper

interface AppContainer {
    val scanHistoryRepository: ScanHistoryRepository
    val qrScannerHelper: QrScannerHelper
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    // Lazy initialization untuk database
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val scanHistoryRepository: ScanHistoryRepository by lazy {
        OfflineScanHistoryRepository(database.scanHistoryDao())
    }

    override val qrScannerHelper: QrScannerHelper by lazy {
        QrScannerHelper(context)
    }
}

