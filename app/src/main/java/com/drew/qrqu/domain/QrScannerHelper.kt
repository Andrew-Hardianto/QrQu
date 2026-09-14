package com.drew.qrqu.domain

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class QrScannerHelper(private val applicationContext: Context) {

    suspend fun scanImageUri(uri: Uri): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val image = InputImage.fromFilePath(applicationContext, uri)
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
                val scanner = BarcodeScanning.getClient(options)

                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            val rawValue = barcodes[0].rawValue
                            if (rawValue != null) {
                                continuation.resume(Result.success(rawValue))
                            } else {
                                continuation.resume(Result.failure(Exception("QR Code kosong atau tidak terbaca")))
                            }
                        } else {
                            continuation.resume(Result.failure(Exception("Tidak ditemukan QR Code pada gambar")))
                        }
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(Result.failure(Exception("Gagal memproses gambar: ${e.message}")))
                    }
            } catch (e: Exception) {
                continuation.resume(Result.failure(Exception("Gagal memuat gambar: ${e.message}")))
            }
        }
    }
}

