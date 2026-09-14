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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drew.qrqu.data.AppDatabase
import com.drew.qrqu.ui.MainViewModel
import com.drew.qrqu.ui.ScanState
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
                    val appContainer = (LocalContext.current.applicationContext as QrQuApplication).container
                    val viewModel: MainViewModel = viewModel(
                        factory = MainViewModel.provideFactory(
                            appContainer.scanHistoryRepository,
                            appContainer.qrScannerHelper
                        )
                    )

                    QrScannerScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun QrScannerScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scanState by viewModel.scanState.collectAsState()
    val scanHistory by viewModel.scanHistory.collectAsState()

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
                viewModel.scanImageUri(uri)
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrutalWhite)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "QR QU",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = BrutalBlack,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                BrutalButton(
                    text = "KAMERA",
                    onClick = {
                        scanner.startScan()
                            .addOnSuccessListener { barcode ->
                                val rawValue = barcode.rawValue
                                if (rawValue != null) {
                                    viewModel.addScannedData(rawValue)
                                } else {
                                    Toast.makeText(context, "QR Code kosong", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                BrutalButton(
                    text = "GALERI",
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // State & History Content
        when (scanState) {
            is ScanState.Loading -> {
                CircularProgressIndicator(color = BrutalBlack, strokeWidth = 4.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("MEMPROSES GAMBAR...", fontWeight = FontWeight.Bold)
            }

            is ScanState.Error -> {
                val message = (scanState as ScanState.Error).message
                BrutalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BrutalButton(
                            text = "Tutup Error",
                            onClick = { viewModel.resetState() }
                        )
                    }
                }
            }

            is ScanState.Idle -> {
                // List History
                if (scanHistory.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "BELUM ADA RIWAYAT SCAN",
                        color = BrutalBlack.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    Text(
                        text = "RIWAYAT SCAN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = BrutalBlack,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(scanHistory) { data ->
                            BrutalCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = data.qrContent,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = BrutalBlack
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        BrutalButton(
                                            text = "COPY",
                                            onClick = { copyToClipboard(context, data.qrContent) },
                                            modifier = Modifier.weight(1f),
                                            textStyle = MaterialTheme.typography.labelSmall,
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                                horizontal = 8.dp,
                                                vertical = 8.dp
                                            )
                                        )

                                        if (android.util.Patterns.WEB_URL.matcher(data.qrContent).matches()) {
                                            BrutalButton(
                                                text = "BUKA",
                                                onClick = { openUrl(context, data.qrContent) },
                                                modifier = Modifier.weight(1f),
                                                textStyle = MaterialTheme.typography.labelSmall,
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                                    horizontal = 8.dp,
                                                    vertical = 8.dp
                                                )
                                            )
                                        }

                                        BrutalButton(
                                            text = "HAPUS",
                                            onClick = {
                                                viewModel.deleteHistory(data)
                                            },
                                            modifier = Modifier.weight(1f),
                                            textStyle = MaterialTheme.typography.labelSmall,
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                                horizontal = 8.dp,
                                                vertical = 8.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: android.content.Context, text: String) {
    val clipboard =
        context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("QR Code", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Disalin ke clipboard", Toast.LENGTH_SHORT).show()
}

private fun openUrl(context: android.content.Context, url: String) {
    var formattedUrl = url
    if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
        formattedUrl = "http://$formattedUrl"
    }
    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(formattedUrl))
    context.startActivity(intent)
}