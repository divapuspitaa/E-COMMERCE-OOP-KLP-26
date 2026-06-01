# DIVERYU — E-Commerce Platform Management System

## Deskripsi
Aplikasi JavaFX dengan tiga role: **Admin**, **Seller**, dan **Customer**.
Tampilan modern bergaya DIVERYU dengan palet hitam & teal.

---

## Cara Menjalankan

### Prasyarat
- **JDK 21** (Java 21)
- **Gradle** (sudah terbundel via `gradlew`)

### Jalankan aplikasi
```bash
# Windows
gradlew.bat run

# macOS / Linux
./gradlew run
```

---

## Demo Akun

| Role     | Username       | Password  |
|----------|---------------|-----------|
| Admin    | admin         | admin123  |
| Seller   | budi_seller   | budi123   |
| Customer | andi_buy      | andi123   |

**Admin Secret (untuk daftar akun Admin):** `OOP26`

---

## Fitur

### Seller
- Melihat daftar produk milik sendiri + statistik nilai inventori
- Menambah produk baru (nama, deskripsi, harga, stok, kategori)
- Mengedit produk via dialog
- Menghapus produk dengan konfirmasi

### Customer
- Melihat semua produk dalam tampilan grid (kartu produk)
- Filter produk berdasarkan kategori (tab navigasi)
- Pencarian produk real-time
- Melihat detail produk (deskripsi, harga, stok)
- Simulasi pembelian dengan pilih jumlah

### Admin
- Overview statistik platform (total user, seller, customer, produk)
- Kelola akun Seller (nonaktifkan / aktifkan / hapus)
- Kelola akun Customer (nonaktifkan / aktifkan / hapus)
- Lihat semua produk yang ada di platform

---

## Struktur Paket
app/src/main/java/proyek/p/

├── App.java                    ← Entry point
│
├── model/
│   ├── User.java               ← Abstract base class (OOP: inheritance)
│   ├── Admin.java
│   ├── Seller.java
│   ├── Customer.java
│   ├── Product.java
│   ├── Transaction.java
│   └── DataStore.java          ← Singleton in-memory storage
│
├── controller/
│   ├── AuthController.java     ← Business logic autentikasi
│   ├── UserController.java     ← Business logic manajemen user
│   ├── ProductController.java  ← Business logic produk
│   └── TransactionController.java ← Business logic transaksi
│
├── dao/
│   ├── UserDAO.java            ← Akses data user
│   ├── ProductDAO.java         ← Akses data produk
│   └── TransactionDAO.java     ← Akses data transaksi
│
├── auth/
│   ├── LoginScreen.java
│   └── RegisterScreen.java
│
├── admin/
│   └── AdminDashboard.java
│
├── seller/
│   └── SellerDashboard.java
│
├── customer/
│   └── CustomerDashboard.java
│
└── ui/
    ├── Theme.java              ← Design tokens
    └── UIFactory.java          ← Reusable UI components
    
## Konsep OOP yang Digunakan
1. Inheritance — Admin, Seller, Customer extends User
2. Abstraction — User adalah abstract class sebagai kerangka dasar seluruh jenis pengguna
3. Encapsulation — semua field dibuat private dan diakses melalui getter/setter
4. Polymorphism — objek User dapat direpresentasikan sebagai Admin, Seller, atau Customer sesuai role

KELOMPOK 26 
1. Erizq Affanditya Nursin_H071251024 - Project Manager
2. Diva Puspita H071251011 - Anggota
3. Muslimah Ayu Hafizhah_H071251070 - Anggota
