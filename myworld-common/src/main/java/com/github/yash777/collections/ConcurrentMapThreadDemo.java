package com.github.yash777.collections;

import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;

public class ConcurrentMapThreadDemo {
    @AllArgsConstructor
    static class Data {
        int value;
        @Override
        public String toString() {
            return "Data{value=" + value +
                    ", identityHash=" + System.identityHashCode(this) + "}";
        }
    }

    static ConcurrentHashMap<String, Data> map = new ConcurrentHashMap<>();

    // approximate bucket index (like internal spread)
    static int bucketIndex(String key, int tableSize) {
        int h = key.hashCode();
        h ^= (h >>> 16);   // spread hash
        return (tableSize - 1) & h;
    }
    static void putKey(String key, int value) {
        Data data = new Data(value);
        if ( map.containsKey(key) ) {
            System.err.println(Thread.currentThread().getName() +
                    " GET key=" + key + " val=" + map.get(key)
            );
        }
        map.putIfAbsent(key, data);

        int bucket = bucketIndex(key, 16); // default table approx
        System.out.println(
                Thread.currentThread().getName() +
                " PUT key=" + key + " val=" + value +
                " hash=" + key.hashCode() + " bucket=" + bucket +
                " identityHash=" + System.identityHashCode(data)
        );
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Initial inserting 4 keys\n");
        for (int i = 1; i <= 4; i++) {
            putKey("T1_Key_" + i, i);
        }

        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 6; i++) {
                //Thread.sleep(3500);
                putKey("T1_Key_" + i, (i * 10));
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            //for (int i = 6; i >= 1; i--) {
            for (int i = 1; i <= 6; i++) {
                putKey("T1_Key_" + i, (i * 100));
            }
        }, "Thread-2");

        t1.start();    t2.start();
        t1.join();     t2.join();

        System.out.println("\nFinal Map Size = " + map.size());
    }
}