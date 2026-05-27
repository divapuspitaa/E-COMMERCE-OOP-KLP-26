# 🛍 NusantaraShop — Aplikasi E-Commerce Desktop

> **Proyek Akhir Lab Pemrograman Berorientasi Objek (PBO)**  
> Semester 2 — Sistem Informasi  
> Tema: **Perdagangan & E-Commerce**  
> Platform: **JavaFX 21 + SQLite**

---

## 📋 Daftar Isi

- [Tentang Aplikasi](#tentang-aplikasi)
- [Fitur Utama](#fitur-utama)
- [Cara Menjalankan](#cara-menjalankan)
- [Akun Demo](#akun-demo)
- [Struktur Kode](#struktur-kode)
- [Penerapan 4 Pilar OOP](#penerapan-4-pilar-oop)
- [Database (SQLite)](#database-sqlite)
- [Teknologi](#teknologi)

---

## Tentang Aplikasi

**NusantaraShop** adalah aplikasi e-commerce desktop berbasis JavaFX yang memungkinkan pengguna membeli produk fisik maupun digital secara lokal. Aplikasi ini mensimulasikan sistem toko online lengkap dengan manajemen produk, keranjang belanja, checkout, hingga panel admin.

### Latar Belakang
Maraknya UMKM lokal yang kesulitan menjual produknya secara digital mendorong perlunya platform yang sederhana, cepat, dan tidak bergantung pada koneksi cloud. NusantaraShop hadir sebagai solusi desktop yang dapat dijalankan secara offline dengan penyimpanan data lokal.

---

## Fitur Utama

### 👤 Pelanggan
| Fitur | Keterangan |
|---|---|
| Login & Registrasi | Autentikasi dengan validasi lengkap |
| Halaman Beranda | Browse produk, search, filter kategori, sort |
| Keranjang Belanja | Tambah/hapus/ubah jumlah item secara dinamis |
| Checkout | Pilih metode pembayaran: Saldo, Transfer, COD |
| Riwayat Pesanan | Lihat semua pesanan beserta status real-time |
| Profil & Top Up | Edit profil, ganti password, isi saldo |

### 🛡 Admin
| Fitur | Keterangan |
|---|---|
| Dashboard | Statistik revenue, pesanan, produk, pengguna |
| Kelola Produk | Tambah, edit, nonaktifkan produk (fisik & digital) |
| Kelola Pesanan | Lihat semua pesanan, ubah status pengiriman |
| Kelola Pengguna | Lihat daftar user, aktifkan/nonaktifkan akun |

---

## Cara Menjalankan

### Prasyarat
- **Java 17+** (disarankan Java 21)
- **Maven 3.8+**

### Langkah

```bash
# 1. Clone repository
git clone https://github.com/<username>/NusantaraShop.git
cd NusantaraShop

# 2. Jalankan dengan Maven JavaFX Plugin
mvn javafx:run

# Atau build dulu lalu jalankan JAR:
mvn clean package -q
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/NusantaraShop-1.0.0.jar
```

> ⚠️ Database SQLite (`nusantarashop.db`) dibuat otomatis di direktori kerja saat pertama kali dijalankan, beserta data awal (seed).

---

## Akun Demo

| Role | Username | Password |
|---|---|---|
| 🛡 Admin | `admin` | `admin123` |
| 🏪 Penjual | `seller1` | `seller123` |
| 👤 Pelanggan | `customer1` | `cust123` |

> Pelanggan demo memiliki saldo awal **Rp 2.000.000** untuk mencoba fitur checkout.

---

## Struktur Kode

```
NusantaraShop/
├── pom.xml                                  # Konfigurasi Maven + dependensi
├── README.md
└── src/main/
    ├── java/
    │   ├── module-info.java                 # Java Module System
    │   └── com/nusantarashop/
    │       ├── MainApp.java                 # Entry point (extends Application)
    │       │
    │       ├── model/                       # ── LAYER MODEL (OOP) ──
    │       │   ├── BaseEntity.java          # Abstract class: id, createdAt, updatedAt
    │       │   ├── User.java                # extends BaseEntity
    │       │   ├── Product.java             # Abstract class extends BaseEntity
    │       │   ├── PhysicalProduct.java     # extends Product (Produk Fisik)
    │       │   ├── DigitalProduct.java      # extends Product (Produk Digital)
    │       │   ├── CartItem.java            # extends BaseEntity
    │       │   └── Order.java               # extends BaseEntity
    │       │
    │       ├── dao/                         # ── LAYER DATA ACCESS ──
    │       │   ├── UserDAO.java             # CRUD tabel users
    │       │   ├── ProductDAO.java          # CRUD tabel products
    │       │   └── OrderDAO.java            # CRUD tabel orders + order_items
    │       │
    │       ├── service/                     # ── LAYER BISNIS ──
    │       │   ├── AuthService.java         # Login, register, logout
    │       │   ├── ProductService.java      # Manajemen produk
    │       │   └── OrderService.java        # Checkout, riwayat pesanan
    │       │
    │       ├── controller/                  # ── LAYER CONTROLLER (JavaFX) ──
    │       │   ├── LoginController.java
    │       │   ├── RegisterController.java
    │       │   ├── HomeController.java
    │       │   ├── CartController.java
    │       │   ├── CheckoutController.java
    │       │   ├── OrderHistoryController.java
    │       │   ├── ProfileController.java
    │       │   ├── AdminDashboardController.java
    │       │   ├── AdminProductsController.java
    │       │   ├── AdminOrdersController.java
    │       │   └── AdminUsersController.java
    │       │
    │       └── util/                        # ── LAYER UTILITAS ──
    │           ├── DatabaseManager.java     # Singleton koneksi SQLite
    │           ├── SessionManager.java      # Singleton sesi + cart in-memory
    │           └── SceneManager.java        # Singleton navigasi antar scene
    │
    └── resources/com/nusantarashop/
        ├── css/
        │   └── style.css                   # Stylesheet global (tema Merah-Putih)
        └── fxml/                           # 11 file tampilan JavaFX
            ├── LoginView.fxml
            ├── RegisterView.fxml
            ├── HomeView.fxml               # Scene utama (Scene 1)
            ├── CartView.fxml               # Scene keranjang (Scene 2)
            ├── CheckoutView.fxml           # Scene checkout (Scene 3)
            ├── OrderHistoryView.fxml       # Scene riwayat (Scene 4)
            ├── ProfileView.fxml            # Scene profil (Scene 5)
            ├── AdminDashboardView.fxml     # Scene admin (Scene 6)
            ├── AdminProductsView.fxml      # Scene admin produk (Scene 7)
            ├── AdminOrdersView.fxml        # Scene admin pesanan (Scene 8)
            └── AdminUsersView.fxml         # Scene admin user (Scene 9)
```

> **11 Scene** — melampaui syarat minimum 2 scene.

---

## Penerapan 4 Pilar OOP

### 1. 🔒 Encapsulation
Seluruh field di semua class model bersifat `private`, hanya dapat diakses melalui **getter** dan **setter**.

```java
// BaseEntity.java, User.java, Product.java, dst.
private String id;
private LocalDateTime createdAt;

public String getId() { return id; }
public void setId(String id) { this.id = id; }
```

Logika bisnis juga dienkapsulasi dalam kelas Service:
```java
// OrderService.java — detail checkout tersembunyi dari Controller
public CheckoutResult checkout(String address, String method, String notes) { ... }
```

---

### 2. 🧬 Inheritance (Pewarisan)

Hierarki pewarisan:

```
BaseEntity  (abstract)
├── User
├── CartItem
├── Order
└── Product  (abstract)
    ├── PhysicalProduct     // Produk fisik (baju, elektronik, dll)
    └── DigitalProduct      // Produk digital (e-book, template, dll)
```

```java
// BaseEntity.java
public abstract class BaseEntity { ... }

// Product.java
public abstract class Product extends BaseEntity { ... }

// PhysicalProduct.java — INHERITANCE dari Product
public class PhysicalProduct extends Product {
    private double weight;
    private double discountPct;
    ...
}
```

---

### 3. 🎭 Abstraction (Abstraksi)

`BaseEntity` dan `Product` adalah **abstract class** yang mendefinisikan kontrak (method abstract) yang wajib diimplementasi semua subclass.

```java
// BaseEntity.java — mendefinisikan kontrak umum
public abstract class BaseEntity {
    public abstract String getDisplayName();  // wajib diimplementasi
    public abstract boolean isValid();        // wajib diimplementasi

    // Template Method — tidak bisa di-override sembarangan
    public final String getSummary() { ... }
}

// Product.java — kontrak tambahan untuk produk
public abstract class Product extends BaseEntity {
    public abstract double getDiscountedPrice();  // harga setelah diskon
    public abstract String getProductType();       // tipe produk
    public abstract boolean canBeShipped();        // bisa dikirim fisik?
}
```

---

### 4. 🔄 Polymorphism (Polimorfisme)

Method yang sama (`getDiscountedPrice`, `canBeShipped`, `getProductType`) berperilaku **berbeda** tergantung tipe objek di runtime.

```java
// PhysicalProduct.java
@Override
public double getDiscountedPrice() {
    // Produk fisik: hitung diskon persen
    if (discountPct <= 0) return getPrice();
    return getPrice() * (1 - discountPct / 100.0);
}

@Override
public boolean canBeShipped() { return true; }  // selalu bisa dikirim

// DigitalProduct.java
@Override
public double getDiscountedPrice() {
    return getPrice();  // produk digital: tidak ada diskon otomatis
}

@Override
public boolean canBeShipped() { return false; }  // tidak perlu kirim fisik
```

Polimorfisme dimanfaatkan di `OrderService` dan `HomeController`:
```java
// OrderService.java — tanpa tahu tipe spesifik produk
boolean hasPhysical = items.stream().anyMatch(i -> !i.isDigital());
double shippingCost = hasPhysical ? 15000 : 0;

// HomeController.java — render berbeda berdasarkan tipe
if (product instanceof PhysicalProduct pp && pp.hasDiscount()) { ... }
if (product instanceof DigitalProduct) { ... }
```

---

## Database (SQLite)

Aplikasi menggunakan **SQLite** via `sqlite-jdbc` dengan 4 tabel:

| Tabel | Deskripsi |
|---|---|
| `users` | Data pengguna (admin, seller, customer) |
| `products` | Produk fisik & digital dalam satu tabel (single-table inheritance) |
| `orders` | Header pesanan |
| `order_items` | Detail item per pesanan |

### Fitur Database
- **Singleton pattern** di `DatabaseManager` — hanya satu koneksi aktif
- **Auto-create** tabel saat pertama kali dijalankan
- **Seed data** otomatis (3 user + 8 produk demo)
- **Transaksi (commit/rollback)** saat menyimpan pesanan
- `PRAGMA foreign_keys = ON` untuk integritas referensial

---

## Teknologi

| Komponen | Teknologi |
|---|---|
| UI Framework | JavaFX 21 |
| Build Tool | Apache Maven 3.8+ |
| Database | SQLite 3 via `sqlite-jdbc` |
| Java Version | Java 17+ (records, switch expression, pattern matching) |
| Arsitektur | MVC (Model-View-Controller) + Service Layer + DAO Layer |
| Design Pattern | Singleton (DB, Session, Scene), Template Method, DAO |

---

## Conventional Commits

Format commit yang digunakan:

```
feat: menambahkan halaman checkout dengan 3 metode pembayaran
feat: implementasi PhysicalProduct dan DigitalProduct (polymorphism)
feat: integrasi SQLite dengan DatabaseManager singleton
fix: memperbaiki validasi form registrasi email kosong
fix: saldo tidak berkurang saat checkout dengan metode SALDO
refactor: ekstrak logika bisnis ke AuthService dan OrderService
style: merapikan tampilan kartu produk dan navbar
docs: menambahkan README dengan penjelasan 4 pilar OOP
```

---

## Kontribusi Tim

| Nama | NIM | Kontribusi |
|---|---|---|
| [Anggota 1] | [NIM] | Model layer, DAO layer, Database |
| [Anggota 2] | [NIM] | Controller Home, Cart, Checkout |
| [Anggota 3] | [NIM] | Admin panel, Service layer |
| [Anggota 4] | [NIM] | UI/CSS design, Login, Register, Profile |

---

*NusantaraShop — Belanja Produk Lokal Terbaik Indonesia 🇮🇩*
