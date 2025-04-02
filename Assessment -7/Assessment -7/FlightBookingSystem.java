import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Flight {
    private int availableSeats;
    private final Lock lock = new ReentrantLock(); 

    public Flight(int seats) {
        this.availableSeats = seats;
    }

    public boolean bookSeat(String passenger) {
        lock.lock();
        try {
            if (availableSeats > 0) {
                availableSeats--;
                System.out.println("Ticket booked for " + passenger + ". Seats left: " + availableSeats);
                return true;
            } else {
                System.out.println(" No seats available for " + passenger);
                return false;
            }
        } finally {
            lock.unlock(); 
        }
    }

    public int getAvailableSeats() {
        return availableSeats;
    }
}


class BookingTask implements Callable<String> {
    private final Flight flight;
    private final String passenger;

    public BookingTask(Flight flight, String passenger) {
        this.flight = flight;
        this.passenger = passenger;
    }

    @Override
    public String call() {
        boolean success = flight.bookSeat(passenger);
        return success ? " Booking Confirmed for " + passenger : " Booking Failed for " + passenger;
    }
}

public class FlightBookingSystem {
    public static void main(String[] args) {
        Flight flight = new Flight(5); 
        ExecutorService executor = Executors.newFixedThreadPool(3); 

        Future<String>[] futures = new Future[10];

        
        for (int i = 0; i < 10; i++) {
            BookingTask task = new BookingTask(flight, "Passenger" + (i + 1));
            futures[i] = executor.submit(task);
        }

        
        for (int i = 0; i < 10; i++) {
            try {
                System.out.println(futures[i].get()); 
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown(); 
    }
}

