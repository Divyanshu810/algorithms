import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface OrderObserver {
    void onOrderStatusChanged(Order order, OrderStatus newStatus);
}

class EmailNotificationService implements OrderObserver {
    @Override
    public void onOrderStatusChanged(Order order, OrderStatus newStatus) {
        String userEmail = order.getUser().getEmail();
        System.out.println("Sending email to " + userEmail + 
                         " - Order " + order.getOrderId() + " status: " + newStatus);
        // In real implementation, integrate with email service
    }
}

class SMSNotificationService implements OrderObserver {
    @Override
    public void onOrderStatusChanged(Order order, OrderStatus newStatus) {
        System.out.println("Sending SMS for Order " + order.getOrderId() + 
                         " status: " + newStatus);
        // In real implementation, integrate with SMS service
    }
}

class InventoryUpdateService implements OrderObserver {
    @Override
    public void onOrderStatusChanged(Order order, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED) {
            // Release reserved inventory
            for (OrderItem item : order.getOrderItems()) {
                item.getProduct().releaseItems(item.getQuantity());
            }
            System.out.println("Inventory released for cancelled order: " + order.getOrderId());
        }
    }
}

class OrderNotificationManager {
    private final List<OrderObserver> observers;
    private final ExecutorService notificationExecutor;
    
    public OrderNotificationManager() {
        this.observers = new CopyOnWriteArrayList<>();
        this.notificationExecutor = Executors.newFixedThreadPool(5);
    }
    
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyOrderStatusChange(Order order, OrderStatus newStatus) {
        // Update order status
        order.setStatus(newStatus);
        
        // Notify all observers asynchronously
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
    
    public void shutdown() {
        notificationExecutor.shutdown();
    }
}