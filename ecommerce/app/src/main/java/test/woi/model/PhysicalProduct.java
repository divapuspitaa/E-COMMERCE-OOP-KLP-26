package test.woi.model;

/**
 * Produk Fisik - mewarisi Product (INHERITANCE).
 * Override getDiscountedPrice() dan canBeShipped() = POLYMORPHISM.
 */
public class PhysicalProduct extends Product {

    private double weight;     // dalam gram
    private double discountPct; // 0-100
    private String dimensions;  // contoh: "30x20x10 cm"

    public PhysicalProduct() {
        super();
        this.discountPct = 0;
    }

    public PhysicalProduct(String id, String name, String description, double price,
                           int stock, Category category, String sellerId,
                           String imageUrl, double rating, int totalReviews,
                           boolean isActive, double weight, double discountPct, String dimensions) {
        super(id, name, description, price, stock, category, sellerId,
              imageUrl, rating, totalReviews, isActive);
        this.weight = weight;
        this.discountPct = discountPct;
        this.dimensions = dimensions;
    }

    @Override
    public double getDiscountedPrice() {
        // POLYMORPHISM: logika diskon untuk produk fisik
        if (discountPct <= 0) return getPrice();
        return getPrice() * (1 - discountPct / 100.0);
    }

    @Override
    public String getProductType() {
        return "Produk Fisik";
    }

    @Override
    public boolean canBeShipped() {
        // Produk fisik selalu bisa dikirim
        return true;
    }

    public double getShippingCostEstimate() {
        // Estimasi ongkir berdasarkan berat (Rp 15.000 per kg)
        double kg = weight / 1000.0;
        return Math.max(kg * 15000, 9000); // minimum Rp 9.000
    }

    public boolean hasDiscount() { return discountPct > 0; }

    public String getDiscountLabel() {
        if (!hasDiscount()) return "";
        return String.format("-%.0f%%", discountPct);
    }

    // Getters & Setters
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getDiscountPct() { return discountPct; }
    public void setDiscountPct(double discountPct) { this.discountPct = discountPct; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
}
