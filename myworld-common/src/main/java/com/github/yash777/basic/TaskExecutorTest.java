package com.github.yash777.basic;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class TaskExecutorTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskExecutorTest.class);
    public static void main(String[] args) throws Exception {
        int taskListSize = 20; // Data Sahred to exexute in threads
        executeTasks(taskListSize, SampleTask.class, 100); // Use 100 threads
        
        //ReentrantLocks.getLocksHolder().releaseLocksHolder(ReentrantLocksContext.CR);
    }

    static List<Runnable> createTasks(int count) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SampleTask task = new SampleTask("Client-" + i);
            tasks.add(task);
        }
        return tasks;
    }

    static void executeTasks(int taskListSize, Class<? extends Runnable> taskClass, int threadPoolSize) throws InterruptedException {
        LOG.info("performTask ClassName:{} Dataset-size:{}", taskClass.getSimpleName(), taskListSize);
        
        long startTime = System.currentTimeMillis();
        List<Runnable> taskList = createTasks(taskListSize); // Simulate 30 tasks
        
        CompletableFuture<?>[] futures = taskList.stream()
                .map(task -> {
                	if (threadPoolSize == 0) {
                		return CompletableFuture.runAsync(task);
                	} else {
                		ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
                		System.out.println("Submitting " + taskList.size() + " tasks with pool size: " + threadPoolSize);
                		
                		return CompletableFuture.runAsync(task, executor);
                	}
                })
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join(); // Wait for all

//        executor.shutdown();
//        executor.awaitTermination(1, TimeUnit.HOURS);

        System.out.println("All tasks completed in " + (System.currentTimeMillis() - startTime) + " ms");
    }

    // Simulates AbstractTask in your setup
    public static abstract class BaseTask implements Runnable {
        
    	private CRReentrantLock lock;
    	
    	protected String clientId;

        public BaseTask(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public void run() {
            lock();
            try {
                performOperation();
            } finally {
                unlock();
                reset();
            }
        }

        private void lock() {
            System.out.println("Locking for client: " + clientId);
        }

        private void unlock() {
            System.out.println("Unlocking for client: " + clientId);
        }

        private void reset() {
            System.out.println("Resetting task for client: " + clientId);
        }

        protected abstract void performOperation();
    }
    public static class CRReentrantLock implements Serializable {
    	
    	private ReentrantLock tlLock;
    	
    	
    }
    // Sample task that sleeps for 5–10 seconds randomly
    public static class SampleTask extends BaseTask {

        public SampleTask(String clientId) {
            super(clientId);
        }

        @Override
        protected void performOperation() {
            try {
                int sleepTime = new Random().nextInt(5000) + 5000; // 5-10 sec
                System.out.println("[" + Thread.currentThread().getName() + "] Started task for " + clientId + ", sleeping " + sleepTime + "ms");
                Thread.sleep(sleepTime);
                System.out.println("[" + Thread.currentThread().getName() + "] Finished task for " + clientId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
