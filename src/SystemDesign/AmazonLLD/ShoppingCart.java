import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ShoppingCart {
    private String userId;
    private Map<String, OrderItem> items; // productId -> OrderItem
    
    public ShoppingCart(String userId) {
        this.userId = userId;
        this.items = new ConcurrentHashMap<>(); // Thread-safe for concurrent access
    }
    
    public synchronized void addItem(Product product, int quantity) {
        String productId = product.getProductId();
        
        if (items.containsKey(productId)) {
            OrderItem existingItem = items.get(productId);
            int newQuantity = existingItem.getQuantity() + quantity;
            items.put(productId, new OrderItem(product, newQuantity));
        } else {
            items.put(productId, new OrderItem(product, quantity));
        }
    }
    
    public synchronized void removeItem(String productId) {
        items.remove(productId);
    }
    
    public synchronized void updateQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            removeItem(productId);
        } else {
            OrderItem item = items.get(productId);
            if (item != null) {
                items.put(productId, new OrderItem(item.getProduct(), quantity));
            }
        }
    }
    
    public synchronized void clearCart() {
        items.clear();
    }
    
    public Map<String, OrderItem> getItems() {
        return new ConcurrentHashMap<>(items);
    }
    
    public double getTotalAmount() {
        return items.values().stream()
            .mapToDouble(OrderItem::getTotalPrice)
            .sum();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
}