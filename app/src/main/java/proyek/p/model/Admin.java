package proyek.p.model;

public class Admin extends User {
    public Admin(String id, String username, String password, String email) {
        super(id, username, password, email, Role.ADMIN);
    }

    @Override
    public String getDashboardTitle() { return "Admin Dashboard"; }
}
