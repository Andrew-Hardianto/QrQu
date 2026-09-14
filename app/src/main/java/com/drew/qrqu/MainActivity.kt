package com.drew.qrqu

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drew.qrqu.ui.MainViewModel
import com.drew.qrqu.ui.UploadState
import com.drew.qrqu.ui.components.BrutalButton
import com.drew.qrqu.ui.components.BrutalCard
import com.drew.qrqu.ui.theme.BrutalBlack
import com.drew.qrqu.ui.theme.BrutalWhite
import com.drew.qrqu.ui.theme.QrQuTheme
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrQuTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BrutalWhite
                ) { innerPadding ->
                    QrScannerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun QrScannerScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val uploadState by viewModel.uploadState.collectAsState()
    val scannedData by viewModel.scannedData.collectAsState()

    // Opsi scanner untuk Play Services (Kamera)
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    
    val scanner = GmsBarcodeScanning.getClient(context, options)

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.scanImageUri(context, uri)
            } else {
                Toast.makeText(context, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrutalWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (scannedData != null) {
            BrutalCard {
                Text(
                    text = "HASIL SCAN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = BrutalBlack,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = scannedData ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BrutalBlack,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        when (uploadState) {
            is UploadState.Idle -> {
                BrutalButton(
                    text = "Scan Kamera",
                    onClick = {
                        scanner.startScan()
                            .addOnSuccessListener { barcode ->
                                val rawValue = barcode.rawValue
                                if (rawValue != null) {
                                    viewModel.setScannedData(rawValue)
                                } else {
                                    Toast.makeText(context, "QR Code kosong", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnCanceledListener {
                                Toast.makeText(context, "Scan dibatalkan", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                BrutalButton(
                    text = "Pilih Galeri",
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
            is UploadState.Loading -> {
                CircularProgressIndicator(color = BrutalBlack, strokeWidth = 4.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("MEMPROSES...", fontWeight = FontWeight.Bold)
            }
            is UploadState.Success -> {
                val message = (uploadState as UploadState.Success).message
                BrutalCard {
                    Text(
                        text = message,
                        color = BrutalBlack,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                BrutalButton(
                    text = "Kembali",
                    onClick = { viewModel.resetState() }
                )
            }
            is UploadState.Error -> {
                val message = (uploadState as UploadState.Error).message
                BrutalCard {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                BrutalButton(
                    text = "Coba Lagi",
                    onClick = { viewModel.resetState() }
                )
            }
        }
    }
}
