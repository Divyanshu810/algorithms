import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface RideObserver {
    void onRideStatusChanged(Ride ride);
}

class RideNotificationService implements RideObserver {
    public void onRideStatusChanged(Ride ride) {
        // Send notifications to rider and driver
    }
}

class RideTrackingService implements RideObserver {
    public void onRideStatusChanged(Ride ride) {
        // Update location tracking
    }
}

class RideSubject {
    private final CopyOnWriteArrayList<RideObserver> observers = new CopyOnWriteArrayList<>();
    private final ExecutorService notificationExecutor = Executors.newFixedThreadPool(5);
    
    public void addObserver(RideObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(RideObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(Ride ride) {
        // Async notification to prevent blocking
        observers.forEach(observer -> 
            notificationExecutor.submit(() -> {
                try {
                    observer.onRideStatusChanged(ride);
                } catch (Exception e) {
                    // Log error but don't fail other notifications
                    System.err.println("Observer notification failed: " + e.getMessage());
                }
            })
        );
    }
    
    public void shutdown() {
        notificationExecutor.shutdown();
    }
}