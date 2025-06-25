package SystemDesign.UberLLD;

import java.util.List;

enum UserType { RIDER, DRIVER }
enum DriverStatus { AVAILABLE, BUSY, OFFLINE }

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