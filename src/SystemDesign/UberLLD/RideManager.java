import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class RideManager {
    private static volatile RideManager instance;
    private final ConcurrentHashMap<String, Ride> activeRides;
    private final ReentrantReadWriteLock lock;
    
    private RideManager() {
        activeRides = new ConcurrentHashMap<>();
        lock = new ReentrantReadWriteLock();
    }
    
    public static RideManager getInstance() {
        if (instance == null) {
            synchronized (RideManager.class) {
                if (instance == null) {
                    instance = new RideManager();
                }
            }
        }
        return instance;
    }
    
    public void addRide(Ride ride) {
        activeRides.put(ride.rideId, ride);
    }
    
    public Ride getRide(String rideId) {
        lock.readLock().lock();
        try {
            return activeRides.get(rideId);
        } finally {
            lock.readLock().unlock();
        }
    }
}