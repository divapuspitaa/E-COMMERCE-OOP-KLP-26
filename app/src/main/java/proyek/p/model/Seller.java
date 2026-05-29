package proyek.p.model;

public class Seller extends User {
    public Seller(String id, String username, String password, String email) {
        super(id, username, password, email, Role.SELLER);
    }

    @Override
    public String getDashboardTitle() { return "Seller Dashboard"; }
}
