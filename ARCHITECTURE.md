# QrQu - Project Architecture & Structure

**Strict MVVM (Model-View-ViewModel)** dengan prinsip **Clean Architecture (Layered)**.

## 1. Konsep Arsitektur
Aplikasi ini membagi *codebase* ke dalam beberapa lapisan (layer) yang terisolasi satu sama lain:
- **UI / View Layer**: Menggunakan **Jetpack Compose** murni. Bertanggung jawab hanya untuk me-render UI dan meneruskan *event* (klik, *input*) ke ViewModel.
- **ViewModel Layer**: Memegang *State* (*StateFlow*) yang diamati (*observed*) oleh UI. Menjadi jembatan antara UI dan lapisan data (*Domain/Repository*). Terisolasi sepenuhnya dari *Android Framework* (tidak memiliki referensi ke `Context`, *Activity*, atau komponen UI).
- **Domain Layer**: Menyimpan *Use Cases* atau *Helper* untuk aturan bisnis yang kompleks atau memanggil perangkat keras/sensor (seperti pembacaan ML Kit QR Scanner).
- **Data Layer (Repository & Source)**: Menyembunyikan kompleksitas basis data (*Room*). Lapisan ini bertugas menyuplai aliran data mentah (*Flow*) kepada ViewModel.
- **Dependency Injection Layer**: Memfasilitasi pembuatan kelas dan dependensi (*Container*) agar kelas-kelas di atas bisa saling disuntikkan (*injected*) secara modular (dapat dites secara terpisah).

---

## 2. Struktur Folder & Paket (Packages)

```text
com.drew.qrqu
│
├── QrQuApplication.kt
│   # Entry point aplikasi (Application class).
│   # Menginisialisasi *AppContainer* saat aplikasi pertama kali dijalankan (Singleton).
│
├── MainActivity.kt
│   # Single-Activity architecture.
│   # Mengatur injeksi ViewModel dari Container dan menampung UI Compose utama (`QrScannerScreen`).
│   # Memiliki fungsi-fungsi helper primitif UI (intent browser & copy clipboard).
│
├── di/ (Dependency Injection)
│   ├── AppContainer.kt
│   # Wadah konfigurasi "Manual Dependency Injection".
│   # Di sinilah Database, Repository, dan Helper di-instansiasi (bersifat 'lazy' atau Singleton).
│
├── domain/ (Business Logic / Use Cases)
│   ├── QrScannerHelper.kt
│   # Memisahkan logika berat pembacaan citra ML Kit Barcode Scanner agar ViewModel 
│   # tidak terkontaminasi oleh pemrosesan Context/Image processing langsung.
│
├── data/ (Data Source & Repository Layer)
│   ├── AppDatabase.kt
│   # Inisialisasi konfigurasi SQLite lokal menggunakan framework Room Database.
│   #
│   ├── ScanHistoryDao.kt
│   # Data Access Object (DAO) untuk melakukan kueri SQL (SELECT, INSERT, DELETE)
│   # secara asinkron menggunakan Coroutines dan Kotlin Flow.
│   #
│   ├── ScanHistoryEntity.kt
│   # Model tabel database SQLite (merepresentasikan 1 baris riwayat scan).
│   #
│   └── repository/
│       ├── ScanHistoryRepository.kt
│       # Antarmuka abstrak (Interface) dan implementasi aslinya (`OfflineScanHistoryRepository`).
│       # Menyembunyikan implementasi DAO dari ViewModel. Memudahkan *mocking* saat unit testing.
│
└── ui/ (Presentation Layer)
    ├── MainViewModel.kt
    # Manajer *state* aplikasi. Memegang `StateFlow` dan memicu eksekusi *Coroutine*.
    # Mengonversi event UI menjadi aksi (tambah/hapus riwayat) yang diteruskan ke Repository.
    # Memiliki `ViewModelProvider.Factory` khusus agar bisa disuntik dependensinya.
    #
    ├── components/
    │   ├── NeoBrutalismComponents.kt
    │   # Komponen UI kustom (seperti `BrutalButton`, `BrutalCard`) yang bisa digunakan ulang.
    #
    └── theme/
        ├── Color.kt, Theme.kt, Type.kt
        # Definisi tema aplikasi berkonsep Neo-Brutalism (warna solid kontras, garis tegas, font tebal).
```

## 3. Alur Data (*Data Flow*)

**Skenario: Men-scan QR melalui Galeri (ML Kit)**
1. Pengguna menekan tombol "GALERI" (UI di `MainActivity`).
2. *Photo Picker* sistem terbuka, pengguna memilih gambar.
3. Gambar (*Uri*) diserahkan ke `viewModel.scanImageUri(uri)`.
4. `MainViewModel` mengubah state UI ke mode *Loading*, kemudian memanggil `QrScannerHelper`.
5. `QrScannerHelper` memproses *Uri* gambar dengan ML Kit (*Context-aware*), dan mengembalikan hasil string QR ke ViewModel menggunakan pola `Result`.
6. Jika sukses, ViewModel memanggil `scanHistoryRepository.insertHistory()`.
7. `ScanHistoryRepository` menyuruh `ScanHistoryDao` untuk mengeksekusi instruksi SQL INSERT.
8. Tabel berubah! Karena Room mengembalikan `Flow`, `ScanHistoryDao` akan otomatis memancarkan versi tabel terbaru.
9. `MainViewModel` (yang terus-menerus memantau/observing Flow ini) menerima *List* sejarah baru dan mengubah `StateFlow`.
10. UI (Jetpack Compose) yang berlangganan (*collectAsState*) ke `scanHistory` akan otomatis di-render ulang (_recomposition_) untuk memunculkan kotak QR yang baru di-scan tersebut.

## 4. Keuntungan Arsitektur Ini
- **Testability**: Sangat mudah menulis pengujian. `MainViewModel` bisa diuji secara terpisah dari *database* (cukup mem-*mock* antarmuka `ScanHistoryRepository`).
- **Scalability**: Jika di kemudian hari timbul kebutuhan mengunggah (sinkronisasi) QR Code ke *Cloud API* (Retrofit), kita hanya perlu memodifikasi `ScanHistoryRepository` tanpa menyentuh *ViewModel* maupun *UI* (Single Source of Truth).
- **Separation of Concerns**: Perubahan warna UI tidak akan memengaruhi logika *database*, begitu juga sebaliknya. Tumpukan *Context* disingkirkan dengan baik.

