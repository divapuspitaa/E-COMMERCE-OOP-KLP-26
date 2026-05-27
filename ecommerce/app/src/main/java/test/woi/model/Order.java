package test.woi.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Order - mewarisi BaseEntity (INHERITANCE).
 * Mewakili transaksi pembelian oleh pelanggan.
 */
public class Order extends BaseEntity {

    public enum Status {
        MENUNGGU_PEMBAYARAN("Menunggu Pembayaran", "#FF9800"),
        DIBAYAR("Sudah Dibayar", "#2196F3"),
        DIPROSES("Sedang Diproses", "#9C27B0"),
        DIKIRIM("Sedang Dikirim", "#00BCD4"),
        SELESAI("Selesai", "#4CAF50"),
        DIBATALKAN("Dibatalkan", "#F44336");

        private final String displayName;
        private final String color;

        Status(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }

    private String orderNumber;
    private String customerId;
    private String customerName;
    private List<CartItem> items;
    private double subtotal;
    private double shippingCost;
    private double totalAmount;
    private Status status;
    private String shippingAddress;
    private String paymentMethod;
    private String notes;
    private LocalDateTime paidAt;

    public Order() {
        super();
        this.items = new ArrayList<>();
        this.status = Status.MENUNGGU_PEMBAYARAN;
        this.orderNumber = generateOrderNumber();
    }

    public Order(String customerId, String customerName, List<CartItem> items,
                 double shippingCost, String shippingAddress, String paymentMethod) {
        super();
        this.orderNumber = generateOrderNumber();
        this.customerId = customerId;
        this.customerName = customerName;
        this.items = new ArrayList<>(items);
        this.shippingCost = shippingCost;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.status = Status.MENUNGGU_PEMBAYARAN;
        recalculateTotal();
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp + "-" + (int)(Math.random() * 1000);
    }

    public void recalculateTotal() {
        this.subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        this.totalAmount = subtotal + shippingCost;
    }

    public void markAsPaid() {
        this.status = Status.DIBAYAR;
        this.paidAt = LocalDateTime.now();
        touch();
    }

    public void updateStatus(Status newStatus) {
        this.status = newStatus;
        touch();
    }

    @Override
    public String getDisplayName() {
        return orderNumber;
    }

    @Override
    public boolean isValid() {
        return customerId != null && items != null && !items.isEmpty() && totalAmount > 0;
    }

    public String getFormattedTotal() { return String.format("Rp %,.0f", totalAmount); }
    public String getFormattedSubtotal() { return String.format("Rp %,.0f", subtotal); }
    public String getFormattedShipping() { return String.format("Rp %,.0f", shippingCost); }
    public int getItemCount() { return items.stream().mapToInt(CartItem::getQuantity).sum(); }

    // Getters & Setters
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; recalculateTotal(); }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getShippingCost() { return shippingCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
