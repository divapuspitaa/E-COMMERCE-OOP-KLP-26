package proyek.p.model;

public abstract class User {
    protected String id;
    protected String username;
    protected String password;
    protected String email;
    protected Role role;
    protected boolean active;

    public enum Role { ADMIN, SELLER, CUSTOMER }

    public User(String id, String username, String password, String email, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.active = true;
    }

    public String getId()         { return id; }
    public String getUsername()   { return username; }
    public String getPassword()   { return password; }
    public String getEmail()      { return email; }
    public Role   getRole()       { return role; }
    public boolean isActive()     { return active; }
    public void setActive(boolean active)     { this.active = active; }
    public void setUsername(String username)  { this.username = username; }
    public void setPassword(String password)  { this.password = password; }

    public abstract String getDashboardTitle();
}
