package SystemDesign.Uber;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RideSharingService {

    private static RideSharingService instance;
    private final Map<String, Driver> drivers;
    private final Map<String, Rider> riders;
    private final Map<String, Trip> trips;


    private RideSharingService(){
        riders = new ConcurrentHashMap<>();
        drivers = new HashMap<>();
        trips = new HashMap<>();
    }

    public static synchronized  RideSharingService getInstance() {
        if(instance == null) {
            instance = new RideSharingService();
        }
        return instance;
    }

    public Driver registerDriver(String name, String contact, String lp, Location location) {
        Driver driver = new Driver();
        drivers.put(driver.getId(), driver);
        return driver;
    }

    public Rider registerRider(String name, String contact) {
        Rider rider = new Rider(name, contact);
        riders.put(rider.getId(), rider);
        return rider;
    }

    public void updateDriverLocation(String driverId, Location location) {
        Driver d = drivers.get(driverId);
        if(d != null) {
            d.updateLocation(location);
        } else
            throw new IllegalArgumentException("No such driver");
    }




}
