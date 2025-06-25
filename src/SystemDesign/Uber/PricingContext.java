package SystemDesign.Uber;

public class PricingContext {
    private PricingStrategy strategy;

    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    public double calculatePrice(Trip t, double di) {
        return strategy.calculateFare(t, di);
    }
}
