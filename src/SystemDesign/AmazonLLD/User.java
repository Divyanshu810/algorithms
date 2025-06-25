import java.util.List;
import java.util.ArrayList;

class User {
    private String userId;
    private String name;
    private String email;
    private String password;
    private List<Order> orders;
    
    public User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.orders = new ArrayList<>();
    }
    
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Order> getOrders() { return new ArrayList<>(orders); }
    
    public void addOrder(Order order) {
        orders.add(order);
    }
}