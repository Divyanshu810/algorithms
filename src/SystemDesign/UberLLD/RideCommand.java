import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

interface RideCommand {
    void execute();
    void undo();
}

class AcceptRideCommand implements RideCommand {
    private Ride ride;
    private Driver driver;
    
    public AcceptRideCommand(Ride ride, Driver driver) {
        this.ride = ride;
        this.driver = driver;
    }
    
    public void execute() {
        ride.driver = driver;
        ride.status = RideStatus.ACCEPTED;
        driver.status = DriverStatus.BUSY;
    }
    
    public void undo() {
        ride.driver = null;
        ride.status = RideStatus.REQUESTED;
        driver.status = DriverStatus.AVAILABLE;
    }
}

class RideCommandInvoker {
    private final ConcurrentLinkedDeque<RideCommand> commandHistory = new ConcurrentLinkedDeque<>();
    private final ReentrantLock commandLock = new ReentrantLock();
    
    public void executeCommand(RideCommand command) {
        commandLock.lock();
        try {
            command.execute();
            commandHistory.push(command);
        } finally {
            commandLock.unlock();
        }
    }
    
    public void undoLastCommand() {
        commandLock.lock();
        try {
            RideCommand lastCommand = commandHistory.pollFirst();
            if (lastCommand != null) {
                lastCommand.undo();
            }
        } finally {
            commandLock.unlock();
        }
    }
}