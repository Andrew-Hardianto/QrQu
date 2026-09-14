# QrQu - Scan & Upload QR

QrQu adalah aplikasi Android modern berbasis **Jetpack Compose** untuk memindai QR Code. Aplikasi ini menggunakan **desain Neo-Brutalism** yang menonjol dan memiliki dua metode pemindaian:
1. **Scan via Kamera**: Menggunakan Google Play Services Code Scanner (tanpa perlu meminta izin kamera secara manual).
2. **Scan via Galeri**: Menggunakan Android Photo Picker dan ML Kit Vision Barcode Scanning untuk membaca QR dari file gambar yang sudah ada.

Hasil pindaian akan langsung disimulasikan untuk diunggah (upload) ke dummy REST API (JSONPlaceholder) menggunakan **Retrofit**.

## Fitur Utama
- 📸 **Kamera Scan (Play Services)**: Pemindaian QR sangat cepat menggunakan UI bawaan Google. Izin kamera dikelola otomatis oleh Play Services.
- 🖼️ **Galeri Scan (ML Kit + Photo Picker)**: Pengguna bisa memilih foto berisi QR Code dengan aman tanpa memerlukan izin akses penyimpanan (`READ_EXTERNAL_STORAGE`).
- 🎨 **UI Neo-Brutalism**: Antarmuka mencolok dengan elemen warna solid (Merah, Putih, Hitam), garis pembatas tebal, dan bayangan blok/pejal (*hard shadow*).
- 🌐 **Network Layer**: Terintegrasi dengan Retrofit 2 dan arsitektur MVVM (Model-View-ViewModel) + Coroutines.
- ✨ **Jetpack Compose**: 100% Native declarative UI.

## Tech Stack
- **Kotlin**
- **Jetpack Compose** (UI)
- **ViewModel & StateFlow** (State Management)
- **Retrofit2 & Gson** (Networking / API)
- **Google ML Kit Barcode Scanning** (Memproses gambar dari galeri)
- **Google Play Services Code Scanner** (Memproses gambar dari kamera)
- **Photo Picker** (`ActivityResultContracts.PickVisualMedia`)

## Cara Menjalankan (Build)
1. *Clone* repositori ini:
   ```bash
   git clone https://github.com/Andrew-Hardianto/QrQu.git
   ```
2. Buka project menggunakan **Android Studio** (Disarankan versi terbaru seperti Ladybug / Koala).
3. Biarkan Gradle melakukan *sync* hingga selesai.
4. Hubungkan perangkat Android fisik atau Emulator. *(Catatan: Fitur Play Services Code Scanner membutuhkan emulator dengan dukungan Google Play).*
5. Tekan tombol **Run** (Shift+F10).

## Arsitektur & Paket
- `ui/`: Komponen presentasi UI, tema, dan `MainViewModel`.
  - `components/`: Berisi custom view `NeoBrutalismComponents.kt` (`BrutalButton`, `BrutalCard`).
  - `theme/`: Berisi palet warna neo-brutalism.
- `data/`: Komponen data dan jaringan (Retrofit `NetworkModule`, `QrApiService`).

## Catatan API
Saat ini URL pengunggahan (*upload*) diset ke dummy API `https://jsonplaceholder.typicode.com/posts`. Apabila Anda ingin mengubah endpoint, silakan buka file `com.drew.qrqu.data.NetworkModule` dan sesuaikan nilai `BASE_URL`.

## Tangkapan Layar (Screenshots)
*(Tambahkan URL gambar screenshot aplikasi Anda di sini nantinya)*

---
*Dibuat oleh [Andrew Hardianto](https://github.com/Andrew-Hardianto)*