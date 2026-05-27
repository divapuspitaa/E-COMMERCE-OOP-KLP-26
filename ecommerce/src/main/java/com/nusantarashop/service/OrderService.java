package com.nusantarashop.service;

import com.nusantarashop.dao.OrderDAO;
import com.nusantarashop.dao.ProductDAO;
import com.nusantarashop.dao.UserDAO;
import com.nusantarashop.model.*;
import com.nusantarashop.util.SessionManager;

import java.util.List;

/**
 * OrderService - logika bisnis untuk checkout dan manajemen pesanan.
 */
public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final UserDAO userDAO = new UserDAO();
    private final SessionManager session = SessionManager.getInstance();

    public record CheckoutResult(boolean success, String message, Order order) {}

    public CheckoutResult checkout(String shippingAddress, String paymentMethod, String notes) {
        if (!session.isLoggedIn())
            return new CheckoutResult(false, "Anda harus login terlebih dahulu.", null);

        List<CartItem> items = session.getCart();
        if (items.isEmpty())
            return new CheckoutResult(false, "Keranjang belanja kosong.", null);

        if (shippingAddress == null || shippingAddress.isBlank())
            return new CheckoutResult(false, "Alamat pengiriman wajib diisi.", null);

        User user = session.getCurrentUser();
        double shippingCost = calculateShipping(items);
        double total = session.getCartTotal() + shippingCost;

        // Cek saldo
        if ("SALDO".equals(paymentMethod) && user.getBalance() < total)
            return new CheckoutResult(false,
                String.format("Saldo tidak cukup. Saldo Anda: Rp %,.0f, Total: Rp %,.0f",
                    user.getBalance(), total), null);

        // Buat order
        Order order = new Order(user.getId(), user.getDisplayName(),
                items, shippingCost, shippingAddress, paymentMethod);
        order.setNotes(notes);

        // Bayar via saldo
        if ("SALDO".equals(paymentMethod)) {
            user.deductBalance(total);
            userDAO.update(user);
            order.markAsPaid();
        }

        // Kurangi stok produk
        for (CartItem item : items) {
            productDAO.findById(item.getProductId()).ifPresent(p -> {
                p.reduceStock(item.getQuantity());
                productDAO.updateStock(p.getId(), p.getStock());
            });
        }

        boolean saved = orderDAO.save(order);
        if (!saved)
            return new CheckoutResult(false, "Gagal menyimpan pesanan. Coba lagi.", null);

        session.clearCart();
        return new CheckoutResult(true,
                "Pesanan berhasil dibuat! No. Pesanan: " + order.getOrderNumber(), order);
    }

    private double calculateShipping(List<CartItem> items) {
        // Semua item digital tidak kena ongkir
        boolean hasPhysical = items.stream().anyMatch(i -> !i.isDigital());
        return hasPhysical ? 15000 : 0;
    }

    public List<Order> getMyOrders() {
        if (!session.isLoggedIn()) return List.of();
        return orderDAO.findByCustomer(session.getCurrentUser().getId());
    }

    public List<Order> getAllOrders() { return orderDAO.findAll(); }

    public boolean updateStatus(String orderId, Order.Status status) {
        return orderDAO.updateStatus(orderId, status);
    }

    public int countOrders() { return orderDAO.countAll(); }
    public double getTotalRevenue() { return orderDAO.sumRevenue(); }
}
