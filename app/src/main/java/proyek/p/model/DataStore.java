package proyek.p.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Singleton in-memory data store for all application data.
 */
public class DataStore {
    private static DataStore instance;
    private final List<User>    users    = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();

    private static final String ADMIN_SECRET = "OOP26";

    private DataStore() { seedData(); }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // ── Seeding ─────────────────────────────────────────────────────────────────
    private void seedData() {
        Admin admin = new Admin("admin-1", "admin", "admin123", "admin@zalora.id");
        users.add(admin);

        Seller seller1 = new Seller("s-1", "budi_seller", "budi123", "budi@email.com");
        Seller seller2 = new Seller("s-2", "siti_shop",   "siti123", "siti@email.com");
        users.add(seller1);
        users.add(seller2);

        Customer cust1 = new Customer("c-1", "andi_buy", "andi123", "andi@email.com");
        Customer cust2 = new Customer("c-2", "dewi_shop","dewi123", "dewi@email.com");
        users.add(cust1);
        users.add(cust2);

        products.add(new Product("p-1","Nike Air Max 270","Sepatu lari pria premium, nyaman untuk olahraga harian.",1_299_000, 15,"Sepatu", "s-1","budi_seller"));
        products.add(new Product("p-2","Adidas Ultraboost 22","Teknologi Boost terbaik untuk performa maksimal.",1_799_000, 8, "Sepatu", "s-1","budi_seller"));
        products.add(new Product("p-3","Under Armour Hoodie","Hoodie sport ringan anti-angin, cocok untuk gym.",449_000,  20,"Pakaian","s-2","siti_shop"));
        products.add(new Product("p-4","Vans Old Skool","Sneaker ikonik dengan sole karet vulkanisir.",699_000,  30,"Sepatu", "s-2","siti_shop"));
        products.add(new Product("p-5","New Balance 574","Sepatu kasual retro klasik pria & wanita.",899_000,  12,"Sepatu", "s-1","budi_seller"));
        products.add(new Product("p-6","Sport Legging Wanita","Legging stretch 4-way, moisture-wicking.",299_000,  25,"Pakaian","s-2","siti_shop"));
    }

    // ── Auth helpers ─────────────────────────────────────────────────────────────
    public boolean validateAdminSecret(String secret) { return ADMIN_SECRET.equals(secret); }

    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username)
                          && u.getPassword().equals(password)
                          && u.isActive())
                .findFirst();
    }

    public boolean usernameExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public void register(User user) { users.add(user); }

    public String generateId() { return UUID.randomUUID().toString().substring(0, 8); }

    // ── User management ──────────────────────────────────────────────────────────
    public List<User> getAllUsers()    { return new ArrayList<>(users); }

    public List<User> getSellers() {
        return users.stream().filter(u -> u.getRole() == User.Role.SELLER).collect(Collectors.toList());
    }

    public List<User> getCustomers() {
        return users.stream().filter(u -> u.getRole() == User.Role.CUSTOMER).collect(Collectors.toList());
    }

    public void deleteUser(String id) {
        users.removeIf(u -> u.getId().equals(id));
        // Also remove seller's products
        products.removeIf(p -> p.getSellerId().equals(id));
    }

    public void setUserActive(String id, boolean active) {
        users.stream().filter(u -> u.getId().equals(id)).findFirst().ifPresent(u -> u.setActive(active));
    }

    // ── Product management ───────────────────────────────────────────────────────
    public List<Product> getAllProducts() { return new ArrayList<>(products); }

    public List<Product> getProductsBySeller(String sellerId) {
        return products.stream().filter(p -> p.getSellerId().equals(sellerId)).collect(Collectors.toList());
    }

    public void addProduct(Product p) { products.add(p); }

    public void deleteProduct(String id) { products.removeIf(p -> p.getId().equals(id)); }

    public Optional<Product> findProduct(String id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }
}
