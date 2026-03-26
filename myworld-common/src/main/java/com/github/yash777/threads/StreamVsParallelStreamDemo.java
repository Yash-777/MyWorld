package com.github.yash777.threads;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Demonstrates the difference between stream() and parallelStream()
 * by executing tasks that sleep for 15 seconds each.
 *
 * Each task prints:
 *   - When the thread started (timestamp)
 *   - Which thread executed it
 *   - When the thread ended (timestamp)
 *   - How long the task took (in seconds)
 *
 * stream() executes sequentially using the main thread.
 * parallelStream() executes concurrently using ForkJoinPool worker threads.
 */
public class StreamVsParallelStreamDemo {

    private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss.SSS");

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

        System.out.println("=== NORMAL STREAM ===");
        long start1 = System.currentTimeMillis();

        numbers.stream().forEach(num -> runTask("STREAM", num));

        long end1 = System.currentTimeMillis();
        System.out.println("Stream total time: " + (end1 - start1) / 1000 + " sec\n");

        // ----------------------------------------------

        System.out.println("=== PARALLEL STREAM ===");
        long start2 = System.currentTimeMillis();

        numbers.parallelStream().forEach(num -> runTask("PARALLEL STREAM", num));

        long end2 = System.currentTimeMillis();
        System.out.println("ParallelStream total time: " + (end2 - start2) / 1000 + " sec\n");
    }

    /**
     * Executes a simulated task that sleeps for 15 seconds.
     *
     * @param type A label describing which stream invoked this task (STREAM / PARALLEL STREAM)
     * @param num  The task identifier
     */
    private static void runTask(String type, int num) {
        String threadName = Thread.currentThread().getName();
        long start = System.currentTimeMillis();
        String startTime = TIME.format(new Date(start));

        System.out.println(type + " → Task " + num + " started on: " +
                threadName + " at " + startTime);

        try {
            Thread.sleep(10000); // Simulate 10-second work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long end = System.currentTimeMillis();
        String endTime = TIME.format(new Date(end));
        long durationSec = (end - start) / 1000;

        System.out.println(type + " → Task " + num + " finished on: " +
                threadName + " at " + endTime +
                " (Duration: " + durationSec + " sec)");
    }
}

