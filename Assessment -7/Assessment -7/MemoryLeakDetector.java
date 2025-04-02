import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class MemoryLeakDetector {
    private static final int MEMORY_THRESHOLD = 80; 
    private static final List<byte[]> memoryLeakList = new ArrayList<>();
    private static final WeakHashMap<Object, String> weakMap = new WeakHashMap<>();

    public static void main(String[] args) {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        
        new Thread(() -> {
            while (true) {
                MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
                long usedMemory = heapMemoryUsage.getUsed();
                long maxMemory = heapMemoryUsage.getMax();
                int usagePercent = (int) ((usedMemory * 100) / maxMemory);

                System.out.println("Memory Usage: " + usagePercent + "%");

                if (usagePercent > MEMORY_THRESHOLD) {
                    System.out.println("WARNING: High memory usage detected!");
                    triggerGarbageCollection();
                }

                try {
                    Thread.sleep(5000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        
        createMemoryLeak();
    }

    private static void createMemoryLeak() {
        for (int i = 0; i < 1000; i++) {
            byte[] leak = new byte[1024 * 1024]; 
            memoryLeakList.add(leak);

            
            weakMap.put(leak, "Cached Value");

            if (i % 100 == 0) {
                System.out.println("Objects added: " + i);
            }
        }
    }

    private static void triggerGarbageCollection() {
        System.out.println("Triggering Garbage Collection...");
        System.gc();
        System.out.println("Garbage Collection Requested.");
    }
}
