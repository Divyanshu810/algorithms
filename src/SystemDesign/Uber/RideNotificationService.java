package SystemDesign.Uber;

public class RideNotificationService implements RideObserver{
    @Override
    public void onRideStatusChange(Trip t) {
        System.out.println("Notifying On Ride Status Change");
    }
}
