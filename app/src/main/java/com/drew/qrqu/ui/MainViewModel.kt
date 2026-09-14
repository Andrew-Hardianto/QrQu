package com.drew.qrqu.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.qrqu.data.ScanHistoryDao
import com.drew.qrqu.data.ScanHistoryEntity
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScanState {
    object Idle : ScanState()
    object Loading : ScanState()
    data class Error(val message: String) : ScanState()
}

class MainViewModel(private val scanHistoryDao: ScanHistoryDao) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    // Mengambil riwayat dari Room Database
    val scanHistory: StateFlow<List<ScanHistoryEntity>> = scanHistoryDao.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Fungsi ini dipanggil dari Camera Scanner (Play Services)
    fun addScannedData(data: String) {
        viewModelScope.launch {
            scanHistoryDao.insertHistory(ScanHistoryEntity(qrContent = data))
        }
    }

    fun deleteHistory(entity: ScanHistoryEntity) {
        viewModelScope.launch {
            scanHistoryDao.deleteHistory(entity)
        }
    }

    // Fungsi ini memproses URI gambar dari Photo Picker (ML Kit)
    fun scanImageUri(context: Context, uri: Uri) {
        _scanState.value = ScanState.Loading
        try {
            val image = InputImage.fromFilePath(context, uri)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val rawValue = barcodes[0].rawValue
                        if (rawValue != null) {
                            addScannedData(rawValue)
                            _scanState.value = ScanState.Idle
                        } else {
                            _scanState.value = ScanState.Error("QR Code kosong atau tidak terbaca")
                        }
                    } else {
                        _scanState.value = ScanState.Error("Tidak ditemukan QR Code pada gambar")
                    }
                }
                .addOnFailureListener { e ->
                    _scanState.value = ScanState.Error("Gagal memproses gambar: ${e.message}")
                }
        } catch (e: Exception) {
            _scanState.value = ScanState.Error("Gagal memuat gambar: ${e.message}")
        }
    }

    fun resetState() {
        _scanState.value = ScanState.Idle
    }
}
