package com.github.yash777.threads;

public class ErrorThreadBehavior {
    public static void main(String[] args) {
        System.out.println("=== Server Simulation: Multiple Request Threads ===\n");
        
        // Simulate 5 concurrent user requests
        Thread request1 = new Thread(() -> handleRequest("User-1", false), "Request-Thread-1");
        Thread request2 = new Thread(() -> handleRequest("User-2", true),  "Request-Thread-2"); // Will throw StackOverflowError
        Thread request3 = new Thread(() -> handleRequest("User-3", false), "Request-Thread-3");
        Thread request4 = new Thread(() -> handleRequest("User-4", false), "Request-Thread-4");
        Thread request5 = new Thread(() -> handleRequest("User-5", false), "Request-Thread-5");
        
        // Start all threads
        request1.start();
        request2.start();
        request3.start();
        request4.start();
        request5.start();
        
        // Main thread continues
        System.out.println("[MAIN] Server main thread continues running...\n");
        
        // Wait for all threads to complete
        try {
            request1.join();
            request2.join(); // This will finish (abnormally)
            request3.join();
            request4.join();
            request5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n[MAIN] All request threads completed. Server still running!");
    }
    
    public static void handleRequest(String user, boolean causeError) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Processing request for " + user);
        
        try {
            Thread.sleep(100); // Simulate processing time
            
            if (causeError) {
                System.out.println("[" + threadName + "] Triggering StackOverflowError...");
                causeStackOverflow(); // This will kill only this thread
            }
            
            System.out.println("[" + threadName + "] ✓ Successfully processed request for " + user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Recursive method to cause StackOverflowError
    public static void causeStackOverflow() {
        causeStackOverflow(); // Infinite recursion
    }
}