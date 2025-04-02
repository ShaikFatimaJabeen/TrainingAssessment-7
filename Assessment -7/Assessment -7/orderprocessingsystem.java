import java.util.concurrent.*;


class Order {
    private final int orderId;
    private final String customerName;

    public Order(int orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }
}


class OrderProcessor {
    public synchronized void processOrder(Order order) {
        System.out.println(Thread.currentThread().getName() + " processing Order ID: " + order.getOrderId() +
                " for " + order.getCustomerName());
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("✅ Order " + order.getOrderId() + " processed successfully!");
    }
}


class OrderWorker implements Runnable {
    private final BlockingQueue<Order> orderQueue;
    private final OrderProcessor orderProcessor;

    public OrderWorker(BlockingQueue<Order> orderQueue, OrderProcessor orderProcessor) {
        this.orderQueue = orderQueue;
        this.orderProcessor = orderProcessor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Order order = orderQueue.take(); 
                orderProcessor.processOrder(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; 
            }
        }
    }
}


public class orderprocessingsystem {
    public static void main(String[] args) {
        BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
        OrderProcessor orderProcessor = new OrderProcessor();
        ExecutorService executor = Executors.newFixedThreadPool(3); 

        
        for (int i = 0; i < 3; i++) {
            executor.submit(new OrderWorker(orderQueue, orderProcessor));
        }

        
        for (int i = 1; i <= 10; i++) {
            Order order = new Order(i, "Customer" + i);
            try {
                orderQueue.put(order); 
                System.out.println("📦 Order " + order.getOrderId() + " placed by " + order.getCustomerName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown(); 
    }
}


