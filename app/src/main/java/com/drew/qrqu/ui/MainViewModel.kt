package com.drew.qrqu.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drew.qrqu.data.NetworkModule
import com.drew.qrqu.data.UploadRequest
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}

class MainViewModel : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    private val _scannedData = MutableStateFlow<String?>(null)
    val scannedData: StateFlow<String?> = _scannedData

    fun setScannedData(data: String) {
        _scannedData.value = data
        uploadData(data)
    }

    fun scanImageUri(context: Context, uri: Uri) {
        _uploadState.value = UploadState.Loading
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
                            setScannedData(rawValue)
                        } else {
                            _uploadState.value = UploadState.Error("QR Code kosong atau tidak terbaca")
                        }
                    } else {
                        _uploadState.value = UploadState.Error("Tidak ditemukan QR Code pada gambar")
                    }
                }
                .addOnFailureListener { e ->
                    _uploadState.value = UploadState.Error("Gagal memproses gambar: ${e.message}")
                }
        } catch (e: Exception) {
             _uploadState.value = UploadState.Error("Gagal memuat gambar: ${e.message}")
        }
    }

    private fun uploadData(data: String) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            try {
                // Proses upload menggunakan Retrofit
                val request = UploadRequest(qrContent = data)
                val response = NetworkModule.apiService.uploadQrData(request)
                _uploadState.value = UploadState.Success("Upload Berhasil! ID: ${response.id}")
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Upload Gagal: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
        _scannedData.value = null
    }
}
