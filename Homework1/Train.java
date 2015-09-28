import java.util.concurrent.atomic.AtomicReference;

public class Train {
    //private Passenger theNullPassenger;
    private AtomicReference<Passenger>[] passengers;
    private boolean stopped = true;
    
    public Train(int capacity) {;
        passengers = new AtomicReference[capacity];
        for (int i = 0; i < passengers.length; i++) {
            passengers[i] = new AtomicReference<Passenger>();
        }
    }

    public boolean isFull() {
        for (int i = 0; i < passengers.length; i++) {
            if (passengers[i].get() == null) {
                return false;
            }
        }
        return true;
    }
    
    public void run() {
        stopped = false;
    }

    public void stop() {
        stopped = true;
    }

    public void clearPassengers() {
        for (int i = 0; i < passengers.length; i++) {
            passengers[i].set(null);
        }
    }

    public boolean addPassenger(Passenger p) {
        if (this.stopped) {
            for (int i = 0; i < passengers.length; i++) {
                if (passengers[i].compareAndSet(null,p)) {
                    return true;
                }
            }
        }
        return false;
    }
}
