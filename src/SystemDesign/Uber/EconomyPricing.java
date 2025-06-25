package SystemDesign.Uber;

class EconomyPricing implements PricingStrategy{
    @Override
    public double calculateFare(Trip trip, double distance) {
        return distance*100;
    }
}
