package com.nusantarashop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager - Singleton untuk koneksi SQLite.
 * Mengelola koneksi dan inisialisasi skema database.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:nusantarashop.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            initializeSchema();
            System.out.println("[DB] Koneksi SQLite berhasil.");
        } catch (Exception e) {
            System.err.println("[DB] Gagal terhubung ke database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Gagal reconnect: " + e.getMessage());
        }
        return connection;
    }

    private void initializeSchema() throws SQLException {
        String[] ddl = {
            // Tabel Users
            """
            CREATE TABLE IF NOT EXISTS users (
                id          TEXT PRIMARY KEY,
                username    TEXT UNIQUE NOT NULL,
                password    TEXT NOT NULL,
                full_name   TEXT,
                email       TEXT,
                phone       TEXT,
                address     TEXT,
                role        TEXT NOT NULL DEFAULT 'CUSTOMER',
                is_active   INTEGER NOT NULL DEFAULT 1,
                balance     REAL NOT NULL DEFAULT 0,
                created_at  TEXT,
                updated_at  TEXT
            )
            """,

            // Tabel Products
            """
            CREATE TABLE IF NOT EXISTS products (
                id              TEXT PRIMARY KEY,
                name            TEXT NOT NULL,
                description     TEXT,
                price           REAL NOT NULL,
                stock           INTEGER NOT NULL DEFAULT 0,
                category        TEXT,
                seller_id       TEXT,
                image_url       TEXT,
                rating          REAL DEFAULT 0,
                total_reviews   INTEGER DEFAULT 0,
                is_active       INTEGER DEFAULT 1,
                product_type    TEXT NOT NULL DEFAULT 'PHYSICAL',
                weight          REAL DEFAULT 0,
                discount_pct    REAL DEFAULT 0,
                dimensions      TEXT,
                download_url    TEXT,
                license_type    TEXT,
                validity_days   INTEGER DEFAULT 0,
                file_format     TEXT,
                created_at      TEXT,
                updated_at      TEXT,
                FOREIGN KEY (seller_id) REFERENCES users(id)
            )
            """,

            // Tabel Orders
            """
            CREATE TABLE IF NOT EXISTS orders (
                id               TEXT PRIMARY KEY,
                order_number     TEXT UNIQUE NOT NULL,
                customer_id      TEXT,
                customer_name    TEXT,
                subtotal         REAL,
                shipping_cost    REAL,
                total_amount     REAL,
                status           TEXT NOT NULL DEFAULT 'MENUNGGU_PEMBAYARAN',
                shipping_address TEXT,
                payment_method   TEXT,
                notes            TEXT,
                paid_at          TEXT,
                created_at       TEXT,
                updated_at       TEXT,
                FOREIGN KEY (customer_id) REFERENCES users(id)
            )
            """,

            // Tabel Order Items
            """
            CREATE TABLE IF NOT EXISTS order_items (
                id               TEXT PRIMARY KEY,
                order_id         TEXT NOT NULL,
                product_id       TEXT NOT NULL,
                product_name     TEXT,
                product_price    REAL,
                discounted_price REAL,
                quantity         INTEGER,
                image_url        TEXT,
                is_digital       INTEGER DEFAULT 0,
                FOREIGN KEY (order_id) REFERENCES orders(id)
            )
            """
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : ddl) {
                stmt.execute(sql);
            }
        }

        seedDefaultData();
    }

    private void seedDefaultData() {
        // Cek apakah admin sudah ada
        try (var rs = connection.createStatement()
                .executeQuery("SELECT COUNT(*) FROM users WHERE role='ADMIN'")) {
            if (rs.getInt(1) > 0) return;
        } catch (SQLException e) {
            return;
        }

        // Seed admin
        String adminId = "admin-001";
        String sellerId = "seller-001";
        String customerId = "customer-001";

        String[] seeds = {
            "INSERT OR IGNORE INTO users (id,username,password,full_name,email,phone,address,role,is_active,balance,created_at,updated_at) VALUES " +
            "('" + adminId + "','admin','admin123','Administrator','admin@nusantarashop.id','081234567890','Jakarta',  'ADMIN',1,0,datetime('now'),datetime('now'))",

            "INSERT OR IGNORE INTO users (id,username,password,full_name,email,phone,address,role,is_active,balance,created_at,updated_at) VALUES " +
            "('" + sellerId + "','seller1','seller123','Budi Santoso','budi@toko.id','081298765432','Bandung','SELLER',1,500000,datetime('now'),datetime('now'))",

            "INSERT OR IGNORE INTO users (id,username,password,full_name,email,phone,address,role,is_active,balance,created_at,updated_at) VALUES " +
            "('" + customerId + "','customer1','cust123','Siti Rahayu','siti@gmail.com','085611223344','Surabaya','CUSTOMER',1,2000000,datetime('now'),datetime('now'))",

            // Produk fisik
            "INSERT OR IGNORE INTO products (id,name,description,price,stock,category,seller_id,image_url,rating,total_reviews,is_active,product_type,weight,discount_pct,dimensions,created_at,updated_at) VALUES " +
            "('prod-001','Kemeja Batik Premium','Kemeja batik tulis halus motif Parang, bahan katun premium 100%',189000,50,'FASHION','" + sellerId + "','','4.8',320,1,'PHYSICAL',350,15,'40x30x2 cm',datetime('now'),datetime('now'))",

            "INSERT OR IGNORE INTO products (id,name,description,price,stock,category,seller_id,image_url,rating,total_reviews,is_active,product_type,weight,discount_pct,dimensions,created_at,updated_at) VALUES " +
            "('prod-002','Headset Gaming RGB','Headset gaming dengan surround sound 7.1, mikrofon noise-cancelling',459000,30,'ELEKTRONIK','" + sellerId + "','','4.5',210,1,'PHYSICAL',450,20,'25x20x10 cm',datetime('now'),datetime('now'))",

            "INSERT OR IGNORE INTO products (id,name,description,price,stock,category,seller_id,image_url,rating,total_reviews,is_active,product_type,weight,discount_pct,dimensions,created_at,updated_at) VALUES " +
            "('prod-003','Kopi Arabika Gayo 250gr','Kopi arabika single origin dari pegunungan Gayo, Aceh. Sangat harum',75000,100,'MAKANAN','" + sellerId + "','','4.9',540,1,'PHYSICAL',260,0,'15x10x5 cm',datetime('now'),datetime('now'))",

            "INSERT OR IGNORE INTO products (id,name,description,price,stock,category,seller_id,image_url,rating,total_reviews,is_active,product_type,weight,discount_pct,dimensions,created_at,updated_at) VALUES " +
            "('prod-004','Sepatu Sneakers Lokal','Sneakers kasual buatan lokal, bahan kanvas premium, nyaman untuk harian',325000,45,'FASHION','" + sellerId + "','','4.6',180,1,'PHYSICAL',800,10,'35x25x15 cm',datetime('now'),datetime('now'))",

            "INSERT OR IGNORE INTO products (id,name,description,price,stock,category,seller_id,image_url,rating,total_reviews,is_active,product_type,weight,discount_pct,dimensions,created_at,updated_at) VALUES " +
            "('prod-005','Tas Anyaman Rotan','Tas anyaman rotan handmade, cocok untuk ke pantai atau acara kasual',245000,25,'FASHION','" + sellerId + "','','4.7',95,1,'PHYSICAL',600,0,'40x30x20 cm',datetime('now'),datetime('now'))",

            "INSERT OR IGNORE INTO products (id,name,description,price,stock,category,seller_id,image_url,rating,total_reviews,is_active,product_type,weight,discount_pct,dimensions,created_at,updated_at) VALUES " +
            "('prod-006','Sambal Roa Manado 200gr','Sambal roa asli Manado, pedas gurih dari ikan roa asap pilihan',55000,80,'MAKANAN','" + sellerId + "','','4.8',430,1,'PHYSICAL',220,0,'10x8x4 cm',datetime('now'),datetime('now'))",

            // Produk digital
            "INSERT OR IGNORE INTO products (id,name,description,price,stock,category,seller_id,image_url,rating,total_reviews,is_active,product_type,download_url,license_type,validity_days,file_format,created_at,updated_at) VALUES " +
            "('prod-007','Template CV ATS Friendly','Koleksi 10 template CV profesional yang lolos ATS, format Word & PDF',35000,999,'BUKU','" + sellerId + "','','4.7',280,1,'DIGITAL','#','SINGLE',0,'ZIP',datetime('now'),datetime('now'))",

            "INSERT OR IGNORE INTO products (id,name,description,price,stock,category,seller_id,image_url,rating,total_reviews,is_active,product_type,download_url,license_type,validity_days,file_format,created_at,updated_at) VALUES " +
            "('prod-008','E-Book Investasi Saham','Panduan lengkap investasi saham untuk pemula hingga mahir, 250 halaman',79000,999,'BUKU','" + sellerId + "','','4.6',175,1,'DIGITAL','#','SINGLE',0,'PDF',datetime('now'),datetime('now'))"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : seeds) {
                stmt.execute(sql);
            }
            System.out.println("[DB] Data awal berhasil ditambahkan.");
        } catch (SQLException e) {
            System.err.println("[DB] Seed error: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Gagal menutup koneksi: " + e.getMessage());
        }
    }
}
