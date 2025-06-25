import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

class Order {
    private static final AtomicLong orderIdGenerator = new AtomicLong(1);
    private String orderId;
    private User user;
    private List<OrderItem> orderItems;
    private double totalAmount;
    private OrderStatus status;
    
    public Order(User user) {
        this.orderId = "ORDER-" + orderIdGenerator.getAndIncrement();
        this.user = user;
        this.orderItems = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.totalAmount = 0.0;
    }
    
    public String getOrderId() { return orderId; }
    public User getUser() { return user; }
    public List<OrderItem> getOrderItems() { return new ArrayList<>(orderItems); }
    public double getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        calculateTotalAmount();
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    private void calculateTotalAmount() {
        totalAmount = orderItems.stream()
            .mapToDouble(OrderItem::getTotalPrice)
            .sum();
    }
}

class OrderItem {
    private Product product;
    private int quantity;
    private double totalPrice;
    
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;
    }
    
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
}