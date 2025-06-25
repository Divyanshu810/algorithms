package SystemDesign.Uber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RideManager {
    private static volatile RideManager instance;
    private final ConcurrentHashMap<String, Trip> activeRides;
    private final ReentrantReadWriteLock lock;

    private RideManager() {
        activeRides = new ConcurrentHashMap<>();
        lock = new ReentrantReadWriteLock();
    }

    public static RideManager getInstance() {
        if(instance == null) {
            synchronized (RideManager.class) {
                if(instance == null) {
                    instance = new RideManager();
                }
            }
        }
        return instance;
    }

    public void addRide(Trip trip) {
        activeRides.put(trip.tripId, trip);
    }
    public Trip getRide(String tripId) {
        lock.readLock().lock();
        try {
            return activeRides.get(tripId);
        } finally {
            lock.readLock().unlock();
        }
    }
}
