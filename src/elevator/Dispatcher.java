package elevator;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Dispatcher extends Thread {

    private final BlockingQueue<Passengers> requests;
    private final List<Elevator> elevators;

    public Dispatcher(
            BlockingQueue<Passengers> requests,
            List<Elevator> elevators
    ) {
        this.requests = requests;
        this.elevators = elevators;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Passengers r = requests.take();

                Elevator e = chooseElevator(r);

                System.out.println(
                        "Dispatcher: request... " +
                        r.fromFloor + " -> " + r.toFloor +
                        " assigned to " + e.getName()
                );

                e.addTarget(r.fromFloor);
                e.addTarget(r.toFloor);

            } catch (InterruptedException e) {}
        }
    }

    private Elevator chooseElevator(Passengers r) {

        Elevator best = null;
        int bestDistance = Integer.MAX_VALUE;

        // first finding the closest free elevator
        for (Elevator e : elevators) {
            if (!e.isBusy()) {
                int dist = Math.abs(e.getCurrentFloor() - r.fromFloor);
                if (dist < bestDistance) {
                    best = e;
                    bestDistance = dist;
                }
            }
        }

       //  if all elevators are busy choosing the closest
        if (best == null) {
            best = elevators.get(0);
            bestDistance =
                    Math.abs(best.getCurrentFloor() - r.fromFloor);

            for (Elevator e : elevators) {
                int dist = Math.abs(e.getCurrentFloor() - r.fromFloor);
                if (dist < bestDistance) {
                    best = e;
                    bestDistance = dist;
                }
            }
        }

        return best;
    }
}

