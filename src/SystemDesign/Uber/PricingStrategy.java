package SystemDesign.Uber;

public interface PricingStrategy {
    double calculateFare(Trip trip, double distance);
}
