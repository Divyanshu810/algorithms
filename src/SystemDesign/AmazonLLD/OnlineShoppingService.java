import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

class OnlineShoppingService {
    private static volatile OnlineShoppingService instance;
    private final Map<String, User> users;
    private final Map<String, Product> products;
    private final Map<String, ShoppingCart> userCarts;
    private final Map<String, List<Order>> userOrders;
    private final Map<String, Sale> activeSales;
    private final ReentrantReadWriteLock lock;
    
    private OnlineShoppingService() {
        users = new ConcurrentHashMap<>();
        products = new ConcurrentHashMap<>();
        userCarts = new ConcurrentHashMap<>();
        userOrders = new ConcurrentHashMap<>();
        activeSales = new ConcurrentHashMap<>();
        lock = new ReentrantReadWriteLock();
    }
    
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
    
    // User Management
    public void registerUser(User user) {
        lock.writeLock().lock();
        try {
            users.put(user.getUserId(), user);
            userCarts.put(user.getUserId(), new ShoppingCart(user.getUserId()));
            userOrders.put(user.getUserId(), new ArrayList<>());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public User getUser(String userId) {
        lock.readLock().lock();
        try {
            return users.get(userId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // Product Management
    public void addProduct(Product product) {
        lock.writeLock().lock();
        try {
            products.put(product.getProductId(), product);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public Product getProduct(String productId) {
        lock.readLock().lock();
        try {
            return products.get(productId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
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
    
    // Shopping Cart Operations
    public void addToCart(String userId, String productId, int quantity) {
        ShoppingCart cart = userCarts.get(userId);
        Product product = getProduct(productId);
        
        if (cart != null && product != null && product.isAvailable()) {
            cart.addItem(product, quantity);
        }
    }
    
    public void removeFromCart(String userId, String productId) {
        ShoppingCart cart = userCarts.get(userId);
        if (cart != null) {
            cart.removeItem(productId);
        }
    }
    
    public ShoppingCart getCart(String userId) {
        return userCarts.get(userId);
    }
    
    // Order Management
    public synchronized Order placeOrder(String userId, Payment payment) {
        User user = getUser(userId);
        ShoppingCart cart = getCart(userId);
        
        if (user == null || cart == null || cart.isEmpty()) {
            return null;
        }
        
        // Check inventory and reserve items
        Map<String, OrderItem> cartItems = cart.getItems();
        for (OrderItem item : cartItems.values()) {
            Product product = item.getProduct();
            if (!product.reserveItems(item.getQuantity())) {
                // Rollback reserved items
                rollbackReservedItems(cartItems, item.getProduct().getProductId());
                return null; // Insufficient inventory\n            }\n        }\n        \n        // Calculate total with discounts\n        double totalAmount = calculateTotalWithDiscounts(cartItems);\n        \n        // Process payment\n        if (!payment.processPayment(totalAmount)) {\n            // Rollback reserved items\n            rollbackReservedItems(cartItems, null);\n            return null;\n        }\n        \n        // Create order\n        Order order = new Order(user);\n        for (OrderItem item : cartItems.values()) {\n            order.addOrderItem(item);\n        }\n        \n        // Add to user's order history\n        userOrders.get(userId).add(order);\n        user.addOrder(order);\n        \n        // Clear cart\n        cart.clearCart();\n        \n        return order;\n    }\n    \n    private void rollbackReservedItems(Map<String, OrderItem> items, String excludeProductId) {\n        for (OrderItem item : items.values()) {\n            if (!item.getProduct().getProductId().equals(excludeProductId)) {\n                item.getProduct().releaseItems(item.getQuantity());\n            }\n        }\n    }\n    \n    private double calculateTotalWithDiscounts(Map<String, OrderItem> items) {\n        double total = 0.0;\n        for (OrderItem item : items.values()) {\n            double itemTotal = item.getTotalPrice();\n            \n            // Apply active sales\n            for (Sale sale : activeSales.values()) {\n                if (sale.isActive()) {\n                    itemTotal = Math.min(itemTotal, \n                        sale.calculateDiscountedPrice(item.getProduct().getPrice(), item.getQuantity()));\n                }\n            }\n            total += itemTotal;\n        }\n        return total;\n    }\n    \n    public List<Order> getUserOrders(String userId) {\n        return userOrders.getOrDefault(userId, new ArrayList<>());\n    }\n    \n    // Sales Management\n    public void addSale(Sale sale) {\n        activeSales.put(sale.getSaleId(), sale);\n    }\n    \n    public void removeSale(String saleId) {\n        activeSales.remove(saleId);\n    }\n    \n    public Map<String, Sale> getActiveSales() {\n        return new HashMap<>(activeSales);\n    }\n    \n    // Inventory Management\n    public boolean updateProductQuantity(String productId, int newQuantity) {\n        Product product = getProduct(productId);\n        if (product != null) {\n            return product.updateQuantity(newQuantity);\n        }\n        return false;\n    }\n    \n    public int getProductQuantity(String productId) {\n        Product product = getProduct(productId);\n        return product != null ? product.getQuantity() : 0;\n    }\n}