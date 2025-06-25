package SystemDesign.Uber;

enum UserType {RIDER, DRIVER};
class User {
    String userId;
    String name, emailId, phone;
    UserType type;
    double rating;
}
