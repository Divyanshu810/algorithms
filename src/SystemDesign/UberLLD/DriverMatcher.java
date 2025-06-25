import java.util.List;

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
    
    private double calculateDistance(Location loc1, Location loc2) {
        // Haversine formula implementation
        return 0.0; // Placeholder
    }
}

class RatingBasedMatcher implements DriverMatchingStrategy {
    public Driver findDriver(Ride ride, List<Driver> drivers) {
        return drivers.stream()
            .filter(d -> d.status == DriverStatus.AVAILABLE)
            .max((d1, d2) -> Double.compare(d1.rating, d2.rating))
            .orElse(null);
    }
}

class RouteBasedMatcher implements DriverMatchingStrategy {
    public Driver findDriver(Ride ride, List<Driver> drivers) {
        // Implementation for shared ride matching
        return drivers.stream()
            .filter(d -> d.status == DriverStatus.AVAILABLE)
            .findFirst()
            .orElse(null);
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