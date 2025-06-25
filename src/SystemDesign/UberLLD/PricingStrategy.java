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