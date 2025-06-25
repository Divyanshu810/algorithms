package SystemDesign.Uber;

import java.time.LocalDateTime;
enum RideType {PREMIUM, ECONOMY}
enum RideStatus {REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED}
public class Trip {
    String tripId;
    Rider rider;
    Driver driver;
    Location src, dest;
    RideType rideType;
    RideStatus rideStatus;
    double fare;
    LocalDateTime createdAt, startTime, endTime;
}
