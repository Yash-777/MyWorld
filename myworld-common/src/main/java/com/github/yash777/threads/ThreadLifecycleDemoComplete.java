package com.github.yash777.threads;

public class ThreadLifecycleDemoComplete {
	static final Object lock = new Object();
	static volatile boolean dataReady = false;
	
	public static void main(String[] args) throws InterruptedException {
		
		// CHILD THREAD (Consumer)
		Thread childThread = new Thread(() -> {
			System.out.println("Child: Started | State: " + Thread.currentThread().getState());
			
			try {
				synchronized(lock) {
					System.out.println("Child: Acquired lock");
					
					while (!dataReady) {
						System.out.println("Child: Data not ready, calling wait()");
						System.out.println("Child: State before wait(): " + Thread.currentThread().getState());
						
						lock.wait();  // WAITING state (releases lock)
						
						System.out.println("Child: Woke up from wait()!");
					}
					
					System.out.println("Child: Data is ready! Processing...");
					System.out.println("Child: State after wake up: " + Thread.currentThread().getState());
				}
				
			} catch (InterruptedException e) {
				System.err.println("Child: Interrupted - " + e.getMessage());
			}
			
			System.out.println("Child: Finished");
		}, "Child-Thread");
		
		
		// MAIN THREAD (Producer)
		System.out.println("Main: Child thread state: " + childThread.getState());  // NEW
		
		childThread.start();
		System.out.println("Main: Child thread state after start(): " + childThread.getState());  // RUNNABLE
		
		Thread.sleep(1000);  // Let child thread enter wait()
		System.out.println("Main: Child thread state (in wait): " + childThread.getState());  // WAITING
		
		// Prepare data
		Thread.sleep(2000);
		System.out.println("\nMain: Preparing data...");
		dataReady = true;
		
		// Notify the waiting thread
		synchronized(lock) {
			System.out.println("Main: Acquired lock");
			System.out.println("Main: Calling notify()");
			lock.notify();  // ✅ Wake up child thread
			System.out.println("Main: notify() called");
			System.out.println("Main: Child thread state (BLOCKED, waiting for lock): " + childThread.getState());
			
			Thread.sleep(1000);  // Hold lock
			System.out.println("Main: Releasing lock...");
		}  // Lock released
		
		System.out.println("Main: Lock released");
		Thread.sleep(500);
		System.out.println("Main: Child thread state (should be RUNNABLE): " + childThread.getState());
		
		childThread.join();
		System.out.println("Main: Child thread state (final): " + childThread.getState());  // TERMINATED
	}
}
