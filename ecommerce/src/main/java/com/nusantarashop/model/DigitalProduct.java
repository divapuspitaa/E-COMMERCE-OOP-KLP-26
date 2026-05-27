package com.nusantarashop.model;

/**
 * Produk Digital - mewarisi Product (INHERITANCE).
 * Override perilaku berbeda untuk produk digital (POLYMORPHISM).
 * Contoh: e-book, software, voucher, template.
 */
public class DigitalProduct extends Product {

    private String downloadUrl;
    private String licenseType;  // "SINGLE", "MULTI", "UNLIMITED"
    private int validityDays;    // 0 = selamanya
    private String fileFormat;   // PDF, MP3, ZIP, dll

    public DigitalProduct() {
        super();
        this.validityDays = 0;
        this.licenseType = "SINGLE";
    }

    public DigitalProduct(String id, String name, String description, double price,
                          int stock, Category category, String sellerId,
                          String imageUrl, double rating, int totalReviews,
                          boolean isActive, String downloadUrl, String licenseType,
                          int validityDays, String fileFormat) {
        super(id, name, description, price, stock, category, sellerId,
              imageUrl, rating, totalReviews, isActive);
        this.downloadUrl = downloadUrl;
        this.licenseType = licenseType;
        this.validityDays = validityDays;
        this.fileFormat = fileFormat;
    }

    @Override
    public double getDiscountedPrice() {
        // POLYMORPHISM: produk digital tidak ada diskon otomatis
        return getPrice();
    }

    @Override
    public String getProductType() {
        return "Produk Digital";
    }

    @Override
    public boolean canBeShipped() {
        // Produk digital tidak perlu pengiriman fisik
        return false;
    }

    public String getValidityText() {
        if (validityDays <= 0) return "Selamanya";
        return validityDays + " Hari";
    }

    public String getLicenseLabel() {
        return switch (licenseType) {
            case "MULTI" -> "Multi Pengguna";
            case "UNLIMITED" -> "Unlimited";
            default -> "Lisensi Tunggal";
        };
    }

    // Getters & Setters
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public int getValidityDays() { return validityDays; }
    public void setValidityDays(int validityDays) { this.validityDays = validityDays; }

    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
}
