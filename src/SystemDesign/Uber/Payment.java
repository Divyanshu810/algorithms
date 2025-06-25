package SystemDesign.Uber;

enum PaymentMethod {CASH, CARD, PAYPAL}
enum PaymentStatus {PENDING, COMPLETED, FAILED}
public class Payment {
    String paymentId;
    double amount;
    PaymentMethod method;
    PaymentStatus status;

}
