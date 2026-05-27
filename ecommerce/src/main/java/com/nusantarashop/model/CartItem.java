package com.nusantarashop.model;

/**
 * CartItem - item dalam keranjang belanja.
 * Mewarisi BaseEntity (INHERITANCE).
 */
public class CartItem extends BaseEntity {

    private String productId;
    private String productName;
    private double productPrice;
    private double discountedPrice;
    private int quantity;
    private String imageUrl;
    private boolean isDigital;

    public CartItem() { super(); }

    public CartItem(Product product, int quantity) {
        super();
        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.discountedPrice = product.getDiscountedPrice();
        this.quantity = quantity;
        this.imageUrl = product.getImageUrl();
        this.isDigital = !product.canBeShipped();
    }

    @Override
    public String getDisplayName() {
        return productName + " x" + quantity;
    }

    @Override
    public boolean isValid() {
        return productId != null && quantity > 0 && discountedPrice >= 0;
    }

    public double getSubtotal() {
        return discountedPrice * quantity;
    }

    public String getFormattedSubtotal() {
        return String.format("Rp %,.0f", getSubtotal());
    }

    public String getFormattedPrice() {
        return String.format("Rp %,.0f", discountedPrice);
    }

    // Getters & Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isDigital() { return isDigital; }
    public void setDigital(boolean digital) { isDigital = digital; }
}
