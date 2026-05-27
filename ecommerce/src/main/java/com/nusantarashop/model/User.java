package com.nusantarashop.model;

/**
 * Model User - mewarisi BaseEntity (INHERITANCE).
 * Menerapkan ENCAPSULATION dengan private fields dan getter/setter.
 */
public class User extends BaseEntity {

    public enum Role {
        ADMIN("Administrator"),
        CUSTOMER("Pelanggan"),
        SELLER("Penjual");

        private final String displayName;
        Role(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private boolean isActive;
    private double balance;

    public User() {
        super();
        this.role = Role.CUSTOMER;
        this.isActive = true;
        this.balance = 0.0;
    }

    public User(String id, String username, String password,
                String fullName, String email, String phone,
                String address, Role role, boolean isActive, double balance) {
        super(id);
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.isActive = isActive;
        this.balance = balance;
    }

    @Override
    public String getDisplayName() {
        return fullName != null ? fullName : username;
    }

    @Override
    public boolean isValid() {
        return username != null && !username.isBlank()
                && password != null && !password.isBlank()
                && email != null && email.contains("@");
    }

    public boolean isAdmin() { return role == Role.ADMIN; }
    public boolean isSeller() { return role == Role.SELLER; }
    public boolean isCustomer() { return role == Role.CUSTOMER; }

    public void addBalance(double amount) {
        if (amount > 0) this.balance += amount;
    }

    public boolean deductBalance(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    // Getters & Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return String.format("User{username='%s', role=%s}", username, role);
    }
}
