package com.github.yash777.threads;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ===============================================================
 *  MultiThreadApplicationAndThreadLoader
 * ===============================================================
 *
 * <h2>Overview</h2>
 * This class demonstrates two important concurrency patterns in Java:
 *
 * <ol>
 *     <li><b>Once-per-application loading</b> – A task that should run
 *         only once for the entire application regardless of how many
 *         threads access it.</li>
 *
 *     <li><b>Once-per-thread loading</b> – A task that should run once
 *         for each thread, even if the same thread invokes it multiple times.</li>
 * </ol>
 *
 * <h2>Implementation</h2>
 * <ul>
 *     <li>{@link MyAppLoader} uses an {@link AtomicBoolean} to ensure that
 *         its load operation executes only once for the entire JVM.</li>
 *
 *     <li>{@link MyThreadLoader} uses a {@link ThreadLocal} to ensure each thread
 *         performs the load operation only once.</li>
 * </ul>
 *
 * <h2>Execution</h2>
 * The {@code main} method creates a fixed thread pool and executes the same task
 * multiple times across multiple threads to demonstrate both behaviors.
 */
public class MultiThreadApplicationAndThreadLoader {

    /**
     * Main method to execute the multithreaded demonstration.
     *
     * @param args command line arguments (not used)
     * @throws InterruptedException if thread termination is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        MyAppLoader loader = new MyAppLoader();
        MyThreadLoader threadLoader = new MyThreadLoader();

        Runnable task = () -> {
            String t = Thread.currentThread().getName();
            System.out.println("Thread started: " + t);

            // Call both loaders multiple times in the same thread
            loader.loadOncePerApplication();
            loader.loadOncePerApplication();

            threadLoader.loadOncePerThread();
            threadLoader.loadOncePerThread();
        };

        // Submit multiple threads
        for (int i = 0; i < 4; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}

/* ===========================================================
   1. LOAD ONCE PER ENTIRE APPLICATION (global singleton logic)
   =========================================================== */

/**
 * Handles operations that must be executed exactly once per application,
 * regardless of how many threads attempt to perform the task.
 *
 * <p>This is achieved using an {@link AtomicBoolean} which guarantees
 * thread-safe state checking and updating without requiring synchronized blocks.</p>
 */
class MyAppLoader {

    /** Tracks if the application-level load has already been executed. */
    private final AtomicBoolean loaded = new AtomicBoolean(false);

    /**
     * Ensures the load operation is executed only once for the entire application.
     *
     * <p>The {@link AtomicBoolean#compareAndSet(boolean, boolean)} method ensures
     * that only the first thread successfully updates the value from false to true.
     * All subsequent threads skip the load operation.</p>
     */
    public void loadOncePerApplication() {
        if (loaded.compareAndSet(false, true)) {
            System.err.println(">>> Application-level load executed");
            heavyLoadFromServer();
        } else {
            System.out.println("Application load skipped (already loaded)");
        }
    }

    /**
     * Simulates an expensive or external resource loading operation.
     * This method is intended to be invoked only once per application.
     */
    private void heavyLoadFromServer() {
        System.out.println("Loading data from server... (application only)");
    }
}

/* ===========================================================
   2. LOAD ONCE PER THREAD (each thread loads once)
   =========================================================== */

/**
 * Handles operations that should be executed once per thread.
 *
 * <p>Uses {@link ThreadLocal} to maintain thread-specific flags so that each
 * thread invokes the load operation only once, even if called repeatedly.</p>
 */
class MyThreadLoader {

    /** Stores whether the current thread has executed its load operation. */
    private final ThreadLocal<Boolean> loadedPerThread =
            ThreadLocal.withInitial(() -> false);

    /**
     * Executes the load operation only once per thread.
     *
     * <p>Each thread maintains its own internal flag via {@link ThreadLocal}.
     * The first call for that thread triggers the load; subsequent calls skip it.</p>
     */
    public void loadOncePerThread() {
        if (!loadedPerThread.get()) {
            System.err.println(">>> Thread-level load executed by: " +Thread.currentThread().getName());
            loadThreadData();
            loadedPerThread.set(true);
        } else {
            System.out.println("Thread load skipped for: " +Thread.currentThread().getName());
        }
    }

    /**
     * Simulates loading data or performing setup that is specific to each thread.
     */
    private void loadThreadData() {
        System.out.println("Loading thread-local data...");
    }
}
