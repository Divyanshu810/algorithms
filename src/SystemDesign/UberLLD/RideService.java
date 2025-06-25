import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class RideRequestException extends RuntimeException {
    public RideRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

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
    
    private double calculateDistance(Location source, Location destination) {
        // Haversine formula implementation
        return 0.0; // Placeholder
    }
}