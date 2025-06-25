package SystemDesign.Uber;

enum DriverStatus {AVAILABLE, BUSY, OFFLINE}
public class Driver extends User {
    private String licenseId;
    Vehicle vehicle;
    DriverStatus status;
}
