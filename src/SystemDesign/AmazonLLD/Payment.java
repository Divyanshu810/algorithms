interface Payment {
    boolean processPayment(double amount);
    String getPaymentDetails();
}

class CreditCardPayment implements Payment {
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    
    public CreditCardPayment(String cardNumber, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }
    
    @Override
    public boolean processPayment(double amount) {
        // Simulate payment processing
        System.out.println("Processing credit card payment of $" + amount);
        // In real implementation, integrate with payment gateway
        return true; // Simulate successful payment
    }
    
    @Override
    public String getPaymentDetails() {
        return "Credit Card ending in " + cardNumber.substring(cardNumber.length() - 4);
    }
}

class PayPalPayment implements Payment {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public boolean processPayment(double amount) {
        System.out.println("Processing PayPal payment of $" + amount + " for " + email);
        return true; // Simulate successful payment
    }
    
    @Override
    public String getPaymentDetails() {
        return "PayPal account: " + email;
    }
}