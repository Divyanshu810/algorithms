import java.time.LocalDateTime;

enum RideType { ECONOMY, PREMIUM, SHARED }
enum RideStatus { REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED }

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