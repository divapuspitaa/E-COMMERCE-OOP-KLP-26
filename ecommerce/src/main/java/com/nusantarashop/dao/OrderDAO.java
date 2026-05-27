package com.nusantarashop.dao;

import com.nusantarashop.model.CartItem;
import com.nusantarashop.model.Order;
import com.nusantarashop.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * OrderDAO - Data Access Object untuk Order dan OrderItems.
 */
public class OrderDAO {

    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public boolean save(Order order) {
        Connection conn = getConn();
        try {
            conn.setAutoCommit(false);

            // Insert order
            String orderSql = """
                INSERT INTO orders (id,order_number,customer_id,customer_name,subtotal,
                    shipping_cost,total_amount,status,shipping_address,payment_method,
                    notes,paid_at,created_at,updated_at)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,datetime('now'),datetime('now'))
                """;
            try (PreparedStatement ps = conn.prepareStatement(orderSql)) {
                ps.setString(1, order.getId());
                ps.setString(2, order.getOrderNumber());
                ps.setString(3, order.getCustomerId());
                ps.setString(4, order.getCustomerName());
                ps.setDouble(5, order.getSubtotal());
                ps.setDouble(6, order.getShippingCost());
                ps.setDouble(7, order.getTotalAmount());
                ps.setString(8, order.getStatus().name());
                ps.setString(9, order.getShippingAddress());
                ps.setString(10, order.getPaymentMethod());
                ps.setString(11, order.getNotes());
                ps.setString(12, order.getPaidAt() != null ? order.getPaidAt().toString() : null);
                ps.executeUpdate();
            }

            // Insert order items
            String itemSql = """
                INSERT INTO order_items (id,order_id,product_id,product_name,
                    product_price,discounted_price,quantity,image_url,is_digital)
                VALUES (?,?,?,?,?,?,?,?,?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                for (CartItem item : order.getItems()) {
                    ps.setString(1, item.getId());
                    ps.setString(2, order.getId());
                    ps.setString(3, item.getProductId());
                    ps.setString(4, item.getProductName());
                    ps.setDouble(5, item.getProductPrice());
                    ps.setDouble(6, item.getDiscountedPrice());
                    ps.setInt(7, item.getQuantity());
                    ps.setString(8, item.getImageUrl());
                    ps.setInt(9, item.isDigital() ? 1 : 0);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.err.println("[OrderDAO] save error: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    public boolean updateStatus(String orderId, Order.Status status) {
        String sql = "UPDATE orders SET status=?, updated_at=datetime('now') WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[OrderDAO] updateStatus error: " + e.getMessage());
            return false;
        }
    }

    public List<Order> findByCustomer(String customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id=? ORDER BY created_at DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = mapOrder(rs);
                o.setItems(findItems(o.getId()));
                orders.add(o);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] findByCustomer error: " + e.getMessage());
        }
        return orders;
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        try (Statement s = getConn().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM orders ORDER BY created_at DESC")) {
            while (rs.next()) {
                Order o = mapOrder(rs);
                o.setItems(findItems(o.getId()));
                orders.add(o);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] findAll error: " + e.getMessage());
        }
        return orders;
    }

    public int countAll() {
        try (Statement s = getConn().createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM orders")) {
            return rs.getInt(1);
        } catch (SQLException e) { return 0; }
    }

    public double sumRevenue() {
        try (Statement s = getConn().createStatement();
             ResultSet rs = s.executeQuery("SELECT COALESCE(SUM(total_amount),0) FROM orders WHERE status='SELESAI'")) {
            return rs.getDouble(1);
        } catch (SQLException e) { return 0; }
    }

    private List<CartItem> findItems(String orderId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setId(rs.getString("id"));
                item.setProductId(rs.getString("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setProductPrice(rs.getDouble("product_price"));
                item.setDiscountedPrice(rs.getDouble("discounted_price"));
                item.setQuantity(rs.getInt("quantity"));
                item.setImageUrl(rs.getString("image_url"));
                item.setDigital(rs.getInt("is_digital") == 1);
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] findItems error: " + e.getMessage());
        }
        return items;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getString("id"));
        o.setOrderNumber(rs.getString("order_number"));
        o.setCustomerId(rs.getString("customer_id"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setSubtotal(rs.getDouble("subtotal"));
        o.setShippingCost(rs.getDouble("shipping_cost"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setNotes(rs.getString("notes"));
        try { o.setStatus(Order.Status.valueOf(rs.getString("status"))); }
        catch (Exception e) { o.setStatus(Order.Status.MENUNGGU_PEMBAYARAN); }
        return o;
    }
}
