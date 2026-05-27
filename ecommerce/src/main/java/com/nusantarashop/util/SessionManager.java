package com.nusantarashop.util;

import com.nusantarashop.model.CartItem;
import com.nusantarashop.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SessionManager - Singleton untuk menyimpan state sesi pengguna.
 * Menyimpan user yang login dan keranjang belanja sementara.
 */
public class SessionManager {

    private static SessionManager instance;

    private User currentUser;
    private List<CartItem> cart;
    private String searchQuery;

    private SessionManager() {
        this.cart = new ArrayList<>();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    // ─── User Session ──────────────────────────────────────────────

    public void login(User user) {
        this.currentUser = user;
        this.cart = new ArrayList<>();
        System.out.println("[Session] Login: " + user.getUsername());
    }

    public void logout() {
        System.out.println("[Session] Logout: " + (currentUser != null ? currentUser.getUsername() : "?"));
        this.currentUser = null;
        this.cart = new ArrayList<>();
        this.searchQuery = null;
    }

    public boolean isLoggedIn() { return currentUser != null; }
    public User getCurrentUser() { return currentUser; }

    public boolean isAdmin() { return isLoggedIn() && currentUser.isAdmin(); }
    public boolean isSeller() { return isLoggedIn() && currentUser.isSeller(); }

    // ─── Cart Management ───────────────────────────────────────────

    public void addToCart(CartItem item) {
        Optional<CartItem> existing = cart.stream()
                .filter(c -> c.getProductId().equals(item.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem c = existing.get();
            c.setQuantity(c.getQuantity() + item.getQuantity());
        } else {
            cart.add(item);
        }
    }

    public void removeFromCart(String productId) {
        cart.removeIf(c -> c.getProductId().equals(productId));
    }

    public void updateCartItemQty(String productId, int newQty) {
        if (newQty <= 0) {
            removeFromCart(productId);
            return;
        }
        cart.stream()
            .filter(c -> c.getProductId().equals(productId))
            .findFirst()
            .ifPresent(c -> c.setQuantity(newQty));
    }

    public void clearCart() { cart.clear(); }

    public List<CartItem> getCart() { return new ArrayList<>(cart); }

    public int getCartCount() {
        return cart.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public double getCartTotal() {
        return cart.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public boolean isInCart(String productId) {
        return cart.stream().anyMatch(c -> c.getProductId().equals(productId));
    }

    // ─── Search State ──────────────────────────────────────────────

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String q) { this.searchQuery = q; }
}
