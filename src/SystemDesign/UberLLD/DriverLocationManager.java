import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

class DriverLocationManager {
    private static volatile DriverLocationManager instance;
    private final ConcurrentHashMap<String, Location> driverLocations;
    private final ScheduledExecutorService locationUpdateExecutor;
    
    private DriverLocationManager() {
        driverLocations = new ConcurrentHashMap<>();
        locationUpdateExecutor = Executors.newScheduledThreadPool(10);
    }
    
    public static DriverLocationManager getInstance() {
        if (instance == null) {
            synchronized (DriverLocationManager.class) {
                if (instance == null) {
                    instance = new DriverLocationManager();
                }
            }
        }
        return instance;
    }
    
    public void updateDriverLocation(String driverId, Location location) {
        driverLocations.put(driverId, location);
        // Broadcast to nearby riders asynchronously
        locationUpdateExecutor.submit(() -> notifyNearbyRiders(driverId, location));
    }
    
    private void notifyNearbyRiders(String driverId, Location location) {
        // Implementation for notifying nearby riders
    }
}