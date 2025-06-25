import java.util.concurrent.atomic.AtomicInteger;

class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private AtomicInteger quantity; // Thread-safe for inventory management
    
    public Product(String productId, String name, String description, double price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = new AtomicInteger(quantity);
    }
    
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity.get(); }
    
    public synchronized boolean updateQuantity(int newQuantity) {
        if (newQuantity < 0) return false;
        quantity.set(newQuantity);
        return true;
    }
    
    public synchronized boolean reserveItems(int count) {
        if (quantity.get() >= count) {
            quantity.addAndGet(-count);
            return true;
        }
        return false;
    }
    
    public synchronized void releaseItems(int count) {
        quantity.addAndGet(count);
    }
    
    public boolean isAvailable() {
        return quantity.get() > 0;
    }
}