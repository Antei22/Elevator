package elevator;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int floorsCount = 0;
        int elevatorsCount = 0;

        while (true) {
            try {
                System.out.print("Enter the number of floors in the building: ");
                floorsCount = sc.nextInt();

                if (floorsCount < 1) {
                    System.out.println("Number of floors must be >= 1");
                    continue;
                }
                break;

            } catch (InputMismatchException e) {
                System.out.println("Enter integer number");
                sc.nextLine(); 
            }
        }

        while (true) {
            try {
                System.out.print("Enter number of elevators in the building: ");
                elevatorsCount = sc.nextInt();

                if (elevatorsCount < 1) {
                    System.out.println("Number of elevators must be >= 1");
                    continue;
                }
                break;

            } catch (InputMismatchException e) {
                System.out.println("Enter integer number");
                sc.nextLine();
            }
        }

        BlockingQueue<Passengers> requests =
                new LinkedBlockingQueue<>();

        List<Elevator> elevators = new ArrayList<>();

        for (int i = 0; i < elevatorsCount; i++) {
            Elevator e = new Elevator("Elevator-" + (i + 1));
            e.start();
            elevators.add(e);
        }

        Dispatcher dispatcher =
                new Dispatcher(requests, elevators);
        dispatcher.start();

        System.out.println(
                "Enter requests: floorFrom floorTo (0 0 ... )"
        );

        while (true) {
            try {
                int from = sc.nextInt();
                int to = sc.nextInt();

                if (from == 0 && to == 0) {
                    System.out.println("Exit");
                    break;
                }

                if (from < 1 || from > floorsCount ||
                    to < 1 || to > floorsCount ||
                    from == to) {

                    System.out.println("Incorrect request");
                    continue;
                }

                requests.add(new Passengers(from, to));

            } catch (InputMismatchException e) {
                System.out.println("Enter two integer numbers");
                sc.nextLine(); 
            }
        }
    }
}

