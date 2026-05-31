package proyek.p.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String id;
    private final String customerId;
    private final String customerName;
    private final String sellerId;
    private final String productId;
    private final String productName;
    private final int    quantity;
    private final double totalPrice;
    private final LocalDateTime createdAt;

    public Transaction(String id, String customerId, String customerName,
                       String sellerId, String productId, String productName,
                       int quantity, double totalPrice) {
        this.id           = id;
        this.customerId   = customerId;
        this.customerName = customerName;
        this.sellerId     = sellerId;
        this.productId    = productId;
        this.productName  = productName;
        this.quantity     = quantity;
        this.totalPrice   = totalPrice;
        this.createdAt    = LocalDateTime.now();
    }

    public String getId()           { return id; }
    public String getCustomerId()   { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getSellerId()     { return sellerId; }
    public String getProductId()    { return productId; }
    public String getProductName()  { return productName; }
    public int    getQuantity()     { return quantity; }
    public double getTotalPrice()   { return totalPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getFormattedTotal() {
        return String.format("Rp %,.0f", totalPrice);
    }

    public String getFormattedDate() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }
}
