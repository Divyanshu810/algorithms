interface DiscountStrategy {
    double applyDiscount(double originalPrice, int quantity);
    String getDiscountDescription();
}

class PercentageDiscount implements DiscountStrategy {
    private double percentage;
    
    public PercentageDiscount(double percentage) {
        this.percentage = percentage;
    }
    
    @Override
    public double applyDiscount(double originalPrice, int quantity) {
        return originalPrice * (1 - percentage / 100);
    }
    
    @Override
    public String getDiscountDescription() {
        return percentage + "% discount";
    }
}

class FixedAmountDiscount implements DiscountStrategy {
    private double discountAmount;
    
    public FixedAmountDiscount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    @Override
    public double applyDiscount(double originalPrice, int quantity) {
        return Math.max(0, originalPrice - discountAmount);
    }
    
    @Override
    public String getDiscountDescription() {
        return "$" + discountAmount + " off";
    }
}

class BuyOneGetOneDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double originalPrice, int quantity) {
        int payableItems = (quantity + 1) / 2;
        return originalPrice * payableItems;
    }
    
    @Override
    public String getDiscountDescription() {
        return "Buy One Get One Free";
    }
}

class Sale {
    private String saleId;
    private String name;
    private DiscountStrategy discountStrategy;
    private boolean isActive;
    
    public Sale(String saleId, String name, DiscountStrategy discountStrategy) {
        this.saleId = saleId;
        this.name = name;
        this.discountStrategy = discountStrategy;
        this.isActive = true;
    }
    
    public String getSaleId() { return saleId; }
    public String getName() { return name; }
    public boolean isActive() { return isActive; }
    
    public void activateSale() { this.isActive = true; }
    public void deactivateSale() { this.isActive = false; }
    
    public double calculateDiscountedPrice(double originalPrice, int quantity) {
        if (!isActive) return originalPrice * quantity;
        return discountStrategy.applyDiscount(originalPrice, quantity);
    }
    
    public String getDiscountDescription() {
        return discountStrategy.getDiscountDescription();
    }
}