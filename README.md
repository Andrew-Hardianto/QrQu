# QrQu - Scan & Simpan QR

QrQu adalah aplikasi Android modern berbasis **Jetpack Compose** untuk memindai QR Code. Aplikasi ini menggunakan **desain Neo-Brutalism** yang menonjol dan memiliki dua metode pemindaian:
1. **Scan via Kamera**: Menggunakan Google Play Services Code Scanner (tanpa perlu meminta izin kamera secara manual).
2. **Scan via Galeri**: Menggunakan Android Photo Picker dan ML Kit Vision Barcode Scanning untuk membaca QR dari file gambar yang sudah ada.

Hasil pindaian akan langsung disimpan secara persisten ke penyimpanan lokal perangkat menggunakan **Room Database**, sehingga riwayat scan tidak akan hilang saat aplikasi ditutup.

## Fitur Utama
- 📸 **Kamera Scan (Play Services)**: Pemindaian QR sangat cepat menggunakan UI bawaan Google. Izin kamera dikelola otomatis oleh Play Services.
- 🖼️ **Galeri Scan (ML Kit + Photo Picker)**: Pengguna bisa memilih foto berisi QR Code dengan aman tanpa memerlukan izin akses penyimpanan (`READ_EXTERNAL_STORAGE`).
- 💾 **Penyimpanan Lokal Persisten**: Setiap hasil scan QR disimpan di tabel database SQLite lokal menggunakan arsitektur **Room**.
- 🎨 **UI Neo-Brutalism**: Antarmuka mencolok dengan elemen warna solid (Merah, Putih, Hitam), garis pembatas tebal, dan bayangan blok/pejal (*hard shadow*).
- ✨ **Jetpack Compose**: 100% Native declarative UI.

## Tech Stack
- **Kotlin 2**
- **Jetpack Compose** (UI)
- **ViewModel, StateFlow & Coroutines** (State Management)
- **Room Database & KSP** (Local Storage / Data Layer)
- **Google ML Kit Barcode Scanning** (Memproses gambar dari galeri)
- **Google Play Services Code Scanner** (Memproses gambar dari kamera)
- **Photo Picker** (`ActivityResultContracts.PickVisualMedia`)

## Cara Menjalankan (Build)
1. *Clone* repositori ini:
   ```bash
   git clone https://github.com/Andrew-Hardianto/QrQu.git
   ```
2. Buka project menggunakan **Android Studio** (Disarankan versi Ladybug / Koala atau lebih baru).
3. Biarkan Gradle melakukan *sync* hingga selesai.
4. Hubungkan perangkat Android fisik atau Emulator. *(Catatan: Fitur Play Services Code Scanner membutuhkan emulator dengan dukungan Google Play).*
5. Tekan tombol **Run** (Shift+F10).

## Arsitektur & Paket
- `ui/`: Komponen presentasi UI, tema, dan `MainViewModel`.
  - `components/`: Berisi custom view `NeoBrutalismComponents.kt` (`BrutalButton`, `BrutalCard`).
  - `theme/`: Berisi palet warna neo-brutalism.
- `data/`: Komponen data lokal.
  - `AppDatabase.kt`: Inisialisasi basis data Room.
  - `ScanHistoryDao.kt`: Data Access Object untuk operasi query ke database.
  - `ScanHistoryEntity.kt`: Model tabel data history scan QR.

## Tangkapan Layar (Screenshots)
*(Tambahkan URL gambar screenshot aplikasi Anda di sini nantinya)*

---
*Dibuat oleh [Andrew Hardianto](https://github.com/Andrew-Hardianto)*