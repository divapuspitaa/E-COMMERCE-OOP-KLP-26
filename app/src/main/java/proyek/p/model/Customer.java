package proyek.p.model;

public class Customer extends User {
    public Customer(String id, String username, String password, String email) {
        super(id, username, password, email, Role.CUSTOMER);
    }

    @Override
    public String getDashboardTitle() { 
        return "Customer Dashboard"; 
    }
}
