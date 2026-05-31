package test.woi.model;

/**
 * Abstract class Product - pilar ABSTRACTION dan dasar INHERITANCE.
 * Subclass akan mengimplementasikan perilaku spesifik (POLYMORPHISM).
 */
public abstract class Product extends BaseEntity {

    public enum Category {
        ELEKTRONIK("Elektronik"),
        FASHION("Fashion & Pakaian"),
        MAKANAN("Makanan & Minuman"),
        KECANTIKAN("Kecantikan"),
        OLAHRAGA("Olahraga"),
        BUKU("Buku & Alat Tulis"),
        RUMAH("Perabot Rumah"),
        OTOMOTIF("Otomotif"),
        LAINNYA("Lainnya");

        private final String displayName;
        Category(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    private String name;
    private String description;
    private double price;
    private int stock;
    private Category category;
    private String sellerId;
    private String imageUrl;
    private double rating;
    private int totalReviews;
    private boolean isActive;

    protected Product() {
        super();
        this.isActive = true;
        this.rating = 0.0;
        this.totalReviews = 0;
    }

    protected Product(String id, String name, String description, double price,
                      int stock, Category category, String sellerId,
                      String imageUrl, double rating, int totalReviews, boolean isActive) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.sellerId = sellerId;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.totalReviews = totalReviews;
        this.isActive = isActive;
    }

    // Abstract methods (ABSTRACTION) - setiap jenis produk punya aturan sendiri
    public abstract double getDiscountedPrice();
    public abstract String getProductType();
    public abstract boolean canBeShipped();

    @Override
    public String getDisplayName() { return name; }

    @Override
    public boolean isValid() {
        return name != null && !name.isBlank()
                && price > 0
                && stock >= 0
                && category != null
                && sellerId != null;
    }

    public boolean isInStock() { return stock > 0 && isActive; }

    public boolean reduceStock(int qty) {
        if (qty > 0 && stock >= qty) {
            stock -= qty;
            touch();
            return true;
        }
        return false;
    }

    public void restoreStock(int qty) {
        if (qty > 0) {
            stock += qty;
            touch();
        }
    }

    public void updateRating(int newRating) {
        double totalScore = this.rating * this.totalReviews + newRating;
        this.totalReviews++;
        this.rating = totalScore / this.totalReviews;
        touch();
    }

    public String getFormattedPrice() {
        return String.format("Rp %,.0f", price);
    }

    public String getFormattedDiscountedPrice() {
        return String.format("Rp %,.0f", getDiscountedPrice());
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return String.format("Product{name='%s', price=%.0f, type=%s}", name, price, getProductType());
    }
}
