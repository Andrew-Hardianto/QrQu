package com.drew.qrqu.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.qrqu.data.ScanHistoryEntity
import com.drew.qrqu.data.repository.ScanHistoryRepository
import com.drew.qrqu.domain.QrScannerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScanState {
    object Idle : ScanState()
    object Loading : ScanState()
    data class Error(val message: String) : ScanState()
}

class MainViewModel(
    private val scanHistoryRepository: ScanHistoryRepository,
    private val qrScannerHelper: QrScannerHelper
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    // Mengambil riwayat dari Repository
    val scanHistory: StateFlow<List<ScanHistoryEntity>> = scanHistoryRepository.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addScannedData(data: String) {
        viewModelScope.launch {
            scanHistoryRepository.insertHistory(ScanHistoryEntity(qrContent = data))
        }
    }

    fun deleteHistory(entity: ScanHistoryEntity) {
        viewModelScope.launch {
            scanHistoryRepository.deleteHistory(entity)
        }
    }

    // Fungsi ini memproses URI gambar melalui Helper
    fun scanImageUri(uri: Uri) {
        _scanState.value = ScanState.Loading
        viewModelScope.launch {
            val result = qrScannerHelper.scanImageUri(uri)
            result.onSuccess { rawValue ->
                addScannedData(rawValue)
                _scanState.value = ScanState.Idle
            }.onFailure { exception ->
                _scanState.value = ScanState.Error(exception.message ?: "Terjadi kesalahan yang tidak diketahui")
            }
        }
    }

    fun resetState() {
        _scanState.value = ScanState.Idle
    }

    companion object {
        fun provideFactory(
            scanHistoryRepository: ScanHistoryRepository,
            qrScannerHelper: QrScannerHelper
        ): androidx.lifecycle.ViewModelProvider.Factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    return MainViewModel(scanHistoryRepository, qrScannerHelper) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
