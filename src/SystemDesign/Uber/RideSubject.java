package SystemDesign.Uber;

import java.util.concurrent.CopyOnWriteArrayList;

public class RideSubject {
    private final CopyOnWriteArrayList<RideObserver> observers = new CopyOnWriteArrayList<>();

}
