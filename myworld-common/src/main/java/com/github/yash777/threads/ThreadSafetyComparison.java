package com.github.yash777.threads;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadSafetyComparison {
	/** Number of CPUS, to place bounds on some sizings */
	static final int NCPU = Runtime.getRuntime().availableProcessors();
	
    private static final int THREAD_COUNT = 4; //12
    private static final int OPERATIONS = 1000;
    
    public static void main(String[] args) throws InterruptedException {
    	System.out.println("THREAD_COUNT :"+THREAD_COUNT);
        System.out.println("=== HashMap (Not Thread-Safe) ===");
        testMap(new HashMap<>());
        
        System.out.println("\n=== Hashtable (Thread-Safe - Synchronized) ===");
        testMap(new Hashtable<>());
        
        System.out.println("\n=== ConcurrentHashMap (Thread-Safe - Lock Striping) ===");
        testMap(new ConcurrentHashMap<>());
    }
    
    private static void testMap(Map<Integer, Integer> map) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        
        Thread[] threads = new Thread[THREAD_COUNT];
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    int key = threadId * OPERATIONS + j;
                    map.put(key, key);
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("Expected size: " + (THREAD_COUNT * OPERATIONS));
        System.out.println("Actual size: " + map.size());
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        System.out.println("Data consistency: " + 
            (map.size() == THREAD_COUNT * OPERATIONS ? "✅ PASS" : "❌ FAIL"));
    }
}
