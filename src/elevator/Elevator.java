package elevator;

import java.util.ArrayList;
import java.util.List;

public class Elevator extends Thread {

    // volatile - cause Dispatcher shoud see current status of the elevator
    private volatile int currentFloor = 1;
    private volatile boolean busy = false;

    private final List<Integer> targets = new ArrayList<>();
    private final Object lock = new Object();

    public Elevator(String name) {
        super(name);
    }

    public void addTarget(int floor) {
        synchronized (lock) {
            if (!targets.contains(floor)) {
                targets.add(floor);
                busy = true; // elevator became busy after getting first target
                System.out.println(getName() + " got target " + floor);
            }
            lock.notify();
        }
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public boolean isBusy() {
        return busy;
    }

    @Override
    public void run() {
        while (true) {
            int baseTarget;

            synchronized (lock) {
                while (targets.isEmpty()) {
                    busy = false;
                    try {
                        lock.wait(); // elevator iswaiting for the target
                    } catch (InterruptedException e) {}
                }
                baseTarget = targets.get(0);
            }

            moveWithStops(baseTarget);
        }
    }

    private void moveWithStops(int baseTarget) {
        while (currentFloor != baseTarget) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}

            if (baseTarget > currentFloor) {
                currentFloor++;
            } else {
                currentFloor--;
            }

            System.out.println(getName() + " floor " + currentFloor + "...");

            Integer stop = findStopOnWay(baseTarget);
            if (stop != null) {
                arrive(stop);
            }
        }

        arrive(baseTarget);
    }

    private Integer findStopOnWay(int baseTarget) {
        synchronized (lock) {
            for (Integer t : targets) {
                if (baseTarget > currentFloor) {
                    if (t > currentFloor && t < baseTarget) {
                        return t;
                    }
                } else {
                    if (t < currentFloor && t > baseTarget) {
                        return t;
                    }
                }
            }
        }
        return null;
    }

    private void arrive(int floor) {
        System.out.println(getName() + " stopped at " + floor);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        synchronized (lock) {
            targets.remove(Integer.valueOf(floor));
            if (targets.isEmpty()) {
                busy = false;
            }
        }
    }
}
