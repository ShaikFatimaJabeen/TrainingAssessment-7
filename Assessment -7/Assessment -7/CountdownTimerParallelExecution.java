import java.util.concurrent.*;


class Task implements Runnable {
    private final String taskName;
    private final CountDownLatch latch;

    public Task(String taskName, CountDownLatch latch) {
        this.taskName = taskName;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            latch.await(); 
            System.out.println(Thread.currentThread().getName() + " Executing: " + taskName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class CountdownTimerParallelExecution {
    public static void main(String[] args) throws InterruptedException {
        int numberOfTasks = 5;
        CountDownLatch latch = new CountDownLatch(3); 
        ExecutorService executor = Executors.newFixedThreadPool(numberOfTasks);

        
        for (int i = 1; i <= numberOfTasks; i++) {
            executor.submit(new Task("Task-" + i, latch));
        }

        
        for (int i = 3; i > 0; i--) {
            System.out.println(" Countdown: " + i);
            Thread.sleep(1000); 
            latch.countDown(); 
        }

        System.out.println(" Countdown Over! Tasks are now executing in parallel.");

        executor.shutdown(); 
    }
}
