package cn.epicmc.foundry.network;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Executor that uses Java 21 virtual threads for network operations.
 * <p>
 * Virtual threads are lightweight threads that allow thousands of concurrent
 * operations without overwhelming the CPU. They are perfect for network operations
 * which may wait on I/O or synchronization.
 * <p>
 * <b>Benefits:</b>
 * <ul>
 *   <li>No thread pool size limits</li>
 *   <li>Extremely low memory overhead per thread</li>
 *   <li>Automatic load balancing across CPU cores</li>
 *   <li>No risk of thread pool exhaustion</li>
 * </ul>
 *
 * @since 1.0.0
 */
public class VirtualThreadExecutor implements Executor {

    private static final VirtualThreadExecutor INSTANCE = new VirtualThreadExecutor();
    private static final AtomicInteger threadCounter = new AtomicInteger(0);

    private VirtualThreadExecutor() {
        // Singleton
    }

    /**
     * Get the singleton instance.
     *
     * @return The executor instance
     */
    public static VirtualThreadExecutor getInstance() {
        return INSTANCE;
    }

    @Override
    public void execute(Runnable command) {
        Thread.ofVirtual()
            .name("foundry-network-" + threadCounter.incrementAndGet())
            .start(command);
    }

    /**
     * Get the current number of virtual threads created.
     * <p>
     * Note: This is the total created, not currently active.
     * Virtual threads are recycled by the JVM automatically.
     *
     * @return Total threads created
     */
    public int getTotalThreadsCreated() {
        return threadCounter.get();
    }

    /**
     * Get an estimate of currently active virtual threads.
     * <p>
     * This counts all threads with the "foundry-network-" prefix.
     *
     * @return Approximate active thread count
     */
    public int getActiveThreadCount() {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        while (rootGroup.getParent() != null) {
            rootGroup = rootGroup.getParent();
        }

        Thread[] threads = new Thread[rootGroup.activeCount()];
        rootGroup.enumerate(threads);

        int count = 0;
        for (Thread thread : threads) {
            if (thread != null && thread.getName().startsWith("foundry-network-")) {
                count++;
            }
        }

        return count;
    }
}
