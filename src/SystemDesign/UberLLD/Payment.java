enum PaymentMethod { CASH, CARD, WALLET }
enum PaymentStatus { PENDING, COMPLETED, FAILED }

class Payment {
    String paymentId;
    double amount;
    PaymentMethod method;
    PaymentStatus status;
}