package com.nusantarashop.dao;

import com.nusantarashop.model.User;
import com.nusantarashop.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserDAO - Data Access Object untuk entitas User.
 * Menangani semua operasi CRUD ke tabel users.
 */
public class UserDAO {

    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] findByUsername error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] authenticate error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean save(User user) {
        String sql = """
            INSERT INTO users (id,username,password,full_name,email,phone,address,
                               role,is_active,balance,created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,datetime('now'),datetime('now'))
            """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getRole().name());
            ps.setInt(9, user.isActive() ? 1 : 0);
            ps.setDouble(10, user.getBalance());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] save error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(User user) {
        String sql = """
            UPDATE users SET full_name=?,email=?,phone=?,address=?,
                            balance=?,is_active=?,updated_at=datetime('now')
            WHERE id=?
            """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getAddress());
            ps.setDouble(5, user.getBalance());
            ps.setInt(6, user.isActive() ? 1 : 0);
            ps.setString(7, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] update error: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT COUNT(*) FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY created_at DESC")) {
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] findAll error: " + e.getMessage());
        }
        return users;
    }

    public int countAll() {
        try (Statement s = getConn().createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
            return rs.getInt(1);
        } catch (SQLException e) { return 0; }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            User.Role.valueOf(rs.getString("role")),
            rs.getInt("is_active") == 1,
            rs.getDouble("balance")
        );
    }
}
