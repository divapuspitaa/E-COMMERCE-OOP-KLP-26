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
| Seller   | siti_shop     | siti123   |
| Customer | andi_buy      | andi123   |
| Customer | dewi_shop     | dewi123   |

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
```
app/src/main/java/proyek/p/
├── App.java                    ← Entry point
├── model/
│   ├── User.java               ← Abstract base class (OOP: inheritance)
│   ├── Admin.java
│   ├── Seller.java
│   ├── Customer.java
│   ├── Product.java
│   └── DataStore.java          ← Singleton in-memory storage
├── auth/
│   ├── LoginScreen.java
│   └── RegisterScreen.java
├── admin/
│   └── AdminDashboard.java
├── seller/
│   └── SellerDashboard.java
├── customer/
│   └── CustomerDashboard.java
└── ui/
    ├── Theme.java              ← Design tokens
    └── UIFactory.java          ← Reusable UI components
```

## Konsep OOP yang Digunakan
- **Inheritance** — `Admin`, `Seller`, `Customer` extends `User`
- **Abstraction** — `User` adalah abstract class dengan method `getDashboardTitle()`
- **Encapsulation** — semua field private dengan getter/setter
- **Polymorphism** — navigasi dashboard menggunakan `switch` pada `user.getRole()`
- **Singleton Pattern** — `DataStore.getInstance()`
