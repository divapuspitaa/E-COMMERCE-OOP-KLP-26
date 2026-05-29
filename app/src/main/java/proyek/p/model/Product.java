package proyek.p.model;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String category;
    private String sellerId;
    private String sellerName;

    public Product(String id, String name, String description, double price, int stock,
                   String category, String sellerId, String sellerName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
    }

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public double getPrice()       { return price; }
    public int    getStock()       { return stock; }
    public String getCategory()    { return category; }
    public String getSellerId()    { return sellerId; }
    public String getSellerName()  { return sellerName; }

    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price)             { this.price = price; }
    public void setStock(int stock)                { this.stock = stock; }
    public void setCategory(String category)       { this.category = category; }

    public String getFormattedPrice() {
        return String.format("Rp %,.0f", price);
    }
}
