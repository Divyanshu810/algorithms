# Amazon eCommerce Website - Low Level Design

## Core Entities

### User Management
```java
class User {
    String userId;
    String name, email, password;
    List<Order> orders;
    
    + getUserId() : String
    + addOrder(Order order) : void
}
```

### Product Management
```java
class Product {
    String productId;
    String name, description;
    double price;
    AtomicInteger quantity; // Thread-safe inventory
    
    + reserveItems(int count) : boolean
    + releaseItems(int count) : void
    + isAvailable() : boolean
}
```

### Order & Cart Management
```java
class Order {
    String orderId;
    User user;
    List<OrderItem> orderItems;
    double totalAmount;
    OrderStatus status;
    
    + addOrderItem(OrderItem item) : void
    + calculateTotalAmount() : void
}

class ShoppingCart {
    String userId;
    Map<String, OrderItem> items; // ConcurrentHashMap for thread-safety
    
    + addItem(Product product, int quantity) : void (synchronized)
    + removeItem(String productId) : void (synchronized)
    + updateQuantity(String productId, int quantity) : void (synchronized)
}
```

## Design Patterns Implementation

### 1. Singleton Pattern - Core Service
```java
class OnlineShoppingService {
    private static volatile OnlineShoppingService instance;
    private final Map<String, User> users; // ConcurrentHashMap
    private final Map<String, Product> products; // ConcurrentHashMap
    private final ReentrantReadWriteLock lock;
    
    public static OnlineShoppingService getInstance() {
        if (instance == null) {
            synchronized (OnlineShoppingService.class) {
                if (instance == null) {
                    instance = new OnlineShoppingService();
                }
            }
        }
        return instance;
    }
    
    public synchronized Order placeOrder(String userId, Payment payment) {
        // Thread-safe order processing with inventory reservation
    }
    
    public void addToCart(String userId, String productId, int quantity) {
        // Concurrent cart operations
    }
}
```

### 2. Strategy Pattern - Discount Management
```java
interface DiscountStrategy {
    double applyDiscount(double originalPrice, int quantity);
    String getDiscountDescription();
}

class PercentageDiscount implements DiscountStrategy {
    private double percentage;
    public double applyDiscount(double originalPrice, int quantity) {
        return originalPrice * (1 - percentage / 100);
    }
}

class BuyOneGetOneDiscount implements DiscountStrategy {
    public double applyDiscount(double originalPrice, int quantity) {
        int payableItems = (quantity + 1) / 2;
        return originalPrice * payableItems;
    }
}

class Sale {
    private DiscountStrategy discountStrategy;
    private boolean isActive;
    
    public double calculateDiscountedPrice(double originalPrice, int quantity) {
        if (!isActive) return originalPrice * quantity;
        return discountStrategy.applyDiscount(originalPrice, quantity);
    }
}
```

### 3. Strategy Pattern - Payment Processing
```java
interface Payment {
    boolean processPayment(double amount);
    String getPaymentDetails();
}

class CreditCardPayment implements Payment {
    private String cardNumber, expiryDate, cvv;
    
    public boolean processPayment(double amount) {
        // Integrate with payment gateway
        return true; // Simulate successful payment
    }
}

class PayPalPayment implements Payment {
    private String email;
    
    public boolean processPayment(double amount) {
        // PayPal integration
        return true;
    }
}
```

### 4. Observer Pattern - Order Notifications (Thread-Safe)
```java
interface OrderObserver {
    void onOrderStatusChanged(Order order, OrderStatus newStatus);
}

class EmailNotificationService implements OrderObserver {
    public void onOrderStatusChanged(Order order, OrderStatus newStatus) {
        // Send email notification
    }
}

class InventoryUpdateService implements OrderObserver {
    public void onOrderStatusChanged(Order order, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED) {
            // Release reserved inventory
            for (OrderItem item : order.getOrderItems()) {
                item.getProduct().releaseItems(item.getQuantity());
            }
        }
    }
}

class OrderNotificationManager {
    private final CopyOnWriteArrayList<OrderObserver> observers;
    private final ExecutorService notificationExecutor;
    
    public void notifyOrderStatusChange(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        
        // Async notification to prevent blocking
        for (OrderObserver observer : observers) {
            notificationExecutor.submit(() -> {
                try {
                    observer.onOrderStatusChanged(order, newStatus);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            });
        }
    }
}
```

### 5. Factory Pattern - Product Creation
```java
abstract class ProductFactory {
    public abstract Product createProduct(String productId, String name, String description, double price, int quantity);
    
    public static ProductFactory getFactory(ProductType type) {
        switch(type) {
            case ELECTRONICS: return new ElectronicsFactory();
            case CLOTHING: return new ClothingFactory();
            case BOOKS: return new BooksFactory();
            default: return new GenericProductFactory();
        }
    }
}

class ElectronicsProduct extends Product {
    private String warranty, brand;
    // Additional electronics-specific properties
}

class ElectronicsFactory extends ProductFactory {
    public Product createProduct(String productId, String name, String description, double price, int quantity) {
        return new ElectronicsProduct(productId, name, description, price, quantity);
    }
}
```

## Core Service Operations

### Thread-Safe Inventory Management
```java
class Product {
    private AtomicInteger quantity;
    
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
}
```

### Concurrent Order Processing
```java
public synchronized Order placeOrder(String userId, Payment payment) {
    // 1. Validate user and cart
    User user = getUser(userId);
    ShoppingCart cart = getCart(userId);
    
    // 2. Reserve inventory (atomic operation)
    Map<String, OrderItem> cartItems = cart.getItems();
    for (OrderItem item : cartItems.values()) {
        if (!item.getProduct().reserveItems(item.getQuantity())) {
            rollbackReservedItems(cartItems, item.getProduct().getProductId());
            return null; // Insufficient inventory
        }
    }
    
    // 3. Calculate total with active sales/discounts
    double totalAmount = calculateTotalWithDiscounts(cartItems);
    
    // 4. Process payment
    if (!payment.processPayment(totalAmount)) {
        rollbackReservedItems(cartItems, null);
        return null;
    }
    
    // 5. Create and finalize order
    Order order = new Order(user);
    for (OrderItem item : cartItems.values()) {
        order.addOrderItem(item);
    }
    
    // 6. Update user history and clear cart
    userOrders.get(userId).add(order);
    cart.clearCart();
    
    return order;
}
```

### Search with Concurrent Access
```java
public List<Product> searchProducts(String keyword) {
    lock.readLock().lock();
    try {
        return products.values().stream()
            .filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                           product.getDescription().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
    } finally {
        lock.readLock().unlock();
    }
}
```

## JavaConcepts.Concurrency Handling Mechanisms

### Thread-Safe Collections
- `ConcurrentHashMap` for users, products, and user carts
- `CopyOnWriteArrayList` for observer lists
- `AtomicInteger` for product inventory counters

### Synchronization
- `ReentrantReadWriteLock` for read-heavy operations (product search)
- `synchronized` methods for cart operations and order placement
- Atomic operations for inventory management

### Async Processing
- `ExecutorService` thread pools for notification processing
- Async observer notifications to prevent blocking
- Non-blocking cart operations where possible

### Sale Time JavaConcepts.Concurrency
- Thread-safe sale activation/deactivation
- Concurrent discount calculations
- Atomic inventory reservations during flash sales

## Key Design Principles Applied

1. **Single Responsibility**: Each class has one clear purpose
2. **Open/Closed**: Easy to add new payment methods, discount strategies, product types
3. **Singleton Pattern**: Global access to core service with thread-safety
4. **Strategy Pattern**: Flexible payment processing and discount algorithms
5. **Observer Pattern**: Decoupled order status notifications with async processing
6. **Factory Pattern**: Clean product creation for different categories
7. **JavaConcepts.Concurrency Control**: Thread-safe operations, atomic inventory management
8. **Inventory Consistency**: Reservation-based system prevents overselling

## Database Schema (Simplified)
```sql
Users: userId, name, email, password, createdAt
Products: productId, name, description, price, quantity, type
Orders: orderId, userId, totalAmount, status, createdAt
OrderItems: itemId, orderId, productId, quantity, unitPrice
ShoppingCarts: cartId, userId, lastUpdated
CartItems: itemId, cartId, productId, quantity, addedAt
Sales: saleId, name, discountType, discountValue, isActive
```

This design provides a robust foundation for an eCommerce platform with strong concurrency support, scalable architecture, and proper use of design patterns for maintainability and extensibility.