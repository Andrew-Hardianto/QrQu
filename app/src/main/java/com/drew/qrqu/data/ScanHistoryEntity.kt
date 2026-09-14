package com.drew.qrqu.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val qrContent: String,
    val timestamp: Long = System.currentTimeMillis()
)

