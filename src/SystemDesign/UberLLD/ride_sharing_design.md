# Ride Sharing App - Low Level Design

## Core Entities

### User Management
```java
class User {
    String userId;
    String name, email, phone;
    Location currentLocation;
    UserType type; // RIDER, DRIVER
    double rating;
}

class Driver extends User {
    String licenseNumber;
    Vehicle vehicle;
    DriverStatus status; // AVAILABLE, BUSY, OFFLINE
    List<String> acceptedRideTypes;
}

class Rider extends User {
    PaymentMethod defaultPayment;
    List<Ride> rideHistory;
}
```

### Ride Management
```java
class Ride {
    String rideId;
    Rider rider;
    Driver driver;
    Location source, destination;
    RideType type; // ECONOMY, PREMIUM, SHARED
    RideStatus status; // REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
    double fare;
    LocalDateTime createdAt, startTime, endTime;
}

class Location {
    double latitude, longitude;
    String address;
}

enum RideType { ECONOMY, PREMIUM, SHARED }
enum RideStatus { REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED }
```

### Vehicle & Payment
```java
class Vehicle {
    String vehicleId, plateNumber;
    VehicleType type; // SEDAN, SUV, HATCHBACK
    String model, color;
}

class Payment {
    String paymentId;
    double amount;
    PaymentMethod method;
    PaymentStatus status;
}
```

## Design Patterns Implementation

### 1. Singleton Pattern - Core Services
```java
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
```

### 2. Strategy Pattern - Pricing
```java
interface PricingStrategy {
    double calculateFare(Ride ride, double distance);
}

class EconomyPricing implements PricingStrategy {
    public double calculateFare(Ride ride, double distance) {
        return distance * 10; // Base rate
    }
}

class SurgePricing implements PricingStrategy {
    private double surgeMultiplier;
    public double calculateFare(Ride ride, double distance) {
        return distance * 10 * surgeMultiplier;
    }
}

class PricingContext {
    private PricingStrategy strategy;
    
    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    
    public double calculatePrice(Ride ride, double distance) {
        return strategy.calculateFare(ride, distance);
    }
}
```

### 3. Observer Pattern - Ride Updates (Thread-Safe)
```java
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
```

### 4. Factory Pattern - Driver Matching
```java
interface DriverMatchingStrategy {
    Driver findDriver(Ride ride, List<Driver> availableDrivers);
}

class NearestDriverMatcher implements DriverMatchingStrategy {
    public Driver findDriver(Ride ride, List<Driver> drivers) {
        return drivers.stream()
            .filter(d -> d.status == DriverStatus.AVAILABLE)
            .min((d1, d2) -> Double.compare(
                calculateDistance(ride.source, d1.currentLocation),
                calculateDistance(ride.source, d2.currentLocation)
            )).orElse(null);
    }
}

class DriverMatcherFactory {
    public static DriverMatchingStrategy getMatchingStrategy(RideType type) {
        switch(type) {
            case PREMIUM: return new RatingBasedMatcher();
            case SHARED: return new RouteBasedMatcher();
            default: return new NearestDriverMatcher();
        }
    }
}
```

### 5. Command Pattern - Ride Operations (Thread-Safe)
```java
interface RideCommand {
    void execute();
    void undo();
}

class AcceptRideCommand implements RideCommand {
    private Ride ride;
    private Driver driver;
    
    public void execute() {
        ride.driver = driver;
        ride.status = RideStatus.ACCEPTED;
        driver.status = DriverStatus.BUSY;
    }
    
    public void undo() {
        ride.driver = null;
        ride.status = RideStatus.REQUESTED;
        driver.status = DriverStatus.AVAILABLE;
    }
}

class RideCommandInvoker {
    private final ConcurrentLinkedDeque<RideCommand> commandHistory = new ConcurrentLinkedDeque<>();
    private final ReentrantLock commandLock = new ReentrantLock();
    
    public void executeCommand(RideCommand command) {
        commandLock.lock();
        try {
            command.execute();
            commandHistory.push(command);
        } finally {
            commandLock.unlock();
        }
    }
    
    public void undoLastCommand() {
        commandLock.lock();
        try {
            RideCommand lastCommand = commandHistory.pollFirst();
            if (lastCommand != null) {
                lastCommand.undo();
            }
        } finally {
            commandLock.unlock();
        }
    }
}
```

## Core Services

### RideService (Thread-Safe with JavaConcepts.Concurrency Control)
```java
class RideService {
    private final DriverMatchingService matchingService;
    private final PricingContext pricingContext;
    private final RideSubject rideSubject;
    private final RideManager rideManager;
    private final Semaphore rideRequestSemaphore; // Limit concurrent ride requests
    private final ExecutorService rideProcessingExecutor;
    
    public RideService() {
        this.rideManager = RideManager.getInstance();
        this.rideRequestSemaphore = new Semaphore(100); // Max 100 concurrent requests
        this.rideProcessingExecutor = Executors.newFixedThreadPool(20);
    }
    
    public CompletableFuture<Ride> requestRide(Rider rider, Location source, Location destination, RideType type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rideRequestSemaphore.acquire();
                
                Ride ride = new Ride(rider, source, destination, type);
                rideManager.addRide(ride);
                
                // Find driver asynchronously
                CompletableFuture<Driver> driverFuture = CompletableFuture.supplyAsync(() -> 
                    matchingService.findAvailableDriver(ride), rideProcessingExecutor);
                
                Driver driver = driverFuture.get(30, TimeUnit.SECONDS); // 30s timeout
                
                if (driver != null) {
                    synchronized (driver) { // Prevent double-booking
                        if (driver.status == DriverStatus.AVAILABLE) {
                            ride.driver = driver;
                            ride.status = RideStatus.ACCEPTED;
                            driver.status = DriverStatus.BUSY;
                            rideSubject.notifyObservers(ride);
                        }
                    }
                }
                return ride;
                
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RideRequestException("Failed to process ride request", e);
            } finally {
                rideRequestSemaphore.release();
            }
        }, rideProcessingExecutor);
    }
    
    public void completeRide(String rideId) {
        Ride ride = rideManager.getRide(rideId);
        if (ride == null) return;
        
        synchronized (ride) {
            if (ride.status != RideStatus.IN_PROGRESS) return;
            
            ride.status = RideStatus.COMPLETED;
            ride.endTime = LocalDateTime.now();
            
            double distance = calculateDistance(ride.source, ride.destination);
            ride.fare = pricingContext.calculatePrice(ride, distance);
            
            // Free up driver
            if (ride.driver != null) {
                synchronized (ride.driver) {
                    ride.driver.status = DriverStatus.AVAILABLE;
                }
            }
            
            rideSubject.notifyObservers(ride);
        }
    }
    
    public void cancelRide(String rideId) {
        Ride ride = rideManager.getRide(rideId);
        if (ride == null) return;
        
        synchronized (ride) {
            if (ride.status == RideStatus.COMPLETED) return;
            
            ride.status = RideStatus.CANCELLED;
            
            // Free up driver if assigned
            if (ride.driver != null) {
                synchronized (ride.driver) {
                    ride.driver.status = DriverStatus.AVAILABLE;
                }
            }
            
            rideSubject.notifyObservers(ride);
        }
    }
}
```

### LocationService (Optimized with JavaConcepts.Concurrency)
```java
class LocationService {
    private final DriverLocationManager locationManager;
    private final ExecutorService locationProcessingExecutor;
    private final Cache<String, List<Driver>> nearbyDriversCache;
    
    public LocationService() {
        this.locationManager = DriverLocationManager.getInstance();
        this.locationProcessingExecutor = Executors.newCachedThreadPool();
        this.nearbyDriversCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();
    }
    
    public void updateDriverLocation(String driverId, Location location) {
        locationManager.updateDriverLocation(driverId, location);
        // Invalidate cache for affected areas
        nearbyDriversCache.invalidateAll();
    }
    
    public CompletableFuture<List<Driver>> getNearbyDrivers(Location location, double radiusKm) {
        String cacheKey = String.format("%.6f_%.6f_%.2f", 
            location.latitude, location.longitude, radiusKm);
        
        List<Driver> cached = nearbyDriversCache.getIfPresent(cacheKey);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            List<Driver> nearbyDrivers = locationManager.getAllDriverLocations()
                .entrySet()
                .parallelStream()
                .filter(entry -> calculateDistance(location, entry.getValue()) <= radiusKm)
                .map(entry -> getDriverById(entry.getKey()))
                .filter(driver -> driver != null && driver.status == DriverStatus.AVAILABLE)
                .collect(Collectors.toList());
            
            nearbyDriversCache.put(cacheKey, nearbyDrivers);
            return nearbyDrivers;
        }, locationProcessingExecutor);
    }
}
```

## JavaConcepts.Concurrency Handling Mechanisms

### Thread-Safe Collections
- `ConcurrentHashMap` for driver locations and active rides
- `CopyOnWriteArrayList` for observer lists
- `ConcurrentLinkedDeque` for command history

### Synchronization
- `ReentrantReadWriteLock` for read-heavy operations
- `ReentrantLock` for command execution
- `synchronized` blocks for critical sections (driver booking, ride status)

### Async Processing
- `CompletableFuture` for non-blocking ride requests
- `ExecutorService` thread pools for location updates and notifications
- `Semaphore` for rate limiting concurrent ride requests

### Caching & Performance
- Caffeine cache for nearby drivers with TTL
- Parallel streams for location filtering
- Async notifications to prevent blocking

## Key Design Principles Applied

1. **Single Responsibility**: Each class has one clear purpose
2. **Open/Closed**: Easy to add new pricing strategies, matching algorithms
3. **Singleton Pattern**: Global access to core managers (RideManager, LocationManager)
4. **Strategy Pattern**: Flexible pricing and driver matching
5. **Observer Pattern**: Decoupled ride status notifications with async processing
6. **Factory Pattern**: Clean driver matching strategy creation
7. **Command Pattern**: Reversible ride operations with thread-safe history
8. **JavaConcepts.Concurrency Control**: Thread-safe operations, rate limiting, async processing

## Database Schema (Simplified)
```sql
Users: userId, name, email, phone, userType, rating
Drivers: userId, licenseNumber, vehicleId, status
Vehicles: vehicleId, plateNumber, type, model
Rides: rideId, riderId, driverId, sourceLocation, destinationLocation, status, fare, createdAt
Locations: locationId, latitude, longitude, address
```

This design provides a solid foundation for a ride-sharing application with clear separation of concerns, extensibility, and proper use of design patterns.