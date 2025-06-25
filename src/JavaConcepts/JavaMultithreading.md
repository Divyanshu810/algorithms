# Java Multithreading Complete Guide

Master Java multithreading from basics to advanced concepts with practical examples and interview-ready explanations.

## Table of Contents
1. [Basic Thread Creation](#1-basic-thread-creation)
2. [Thread Synchronization](#2-thread-synchronization)
3. [Producer-Consumer Pattern](#3-producer-consumer-pattern)
4. [Thread Pools](#4-thread-pools)
5. [CompletableFuture](#5-completablefuture)
6. [Download Manager Example](#6-real-world-example-download-manager)
7. [Locking Mechanisms](#7-locking-mechanisms)
8. [ReadWriteLock](#8-readwritelock)
9. [Atomic Variables](#9-atomic-variables)
10. [Deadlock Prevention](#10-deadlock-prevention)
11. [Condition Variables](#11-condition-variables)
12. [Thread-Safe Singleton](#12-thread-safe-singleton)

## 1. Basic Thread Creation

### Method 1: Extending Thread Class
```java
class MyTask extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Task running: " + i);
            try {
                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Usage:
MyTask task = new MyTask();
task.start(); // Don't call run() directly!
```

### Method 2: Implementing Runnable (Preferred)
```java
class PrintNumbers implements Runnable {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Usage:
Thread thread1 = new Thread(new PrintNumbers(), "Worker-1");
Thread thread2 = new Thread(new PrintNumbers(), "Worker-2");
thread1.start();
thread2.start();
```

**Why Runnable is better:**
- Java doesn't support multiple inheritance, but you can implement multiple interfaces
- Separation of task (Runnable) from thread management (Thread)
- Better design for object-oriented programming

## 2. Thread Synchronization

### Problem: Race Condition
```java
class Counter {
    private int count = 0;
    
    public void increment() {
        count++; // NOT THREAD-SAFE!
        // This is actually 3 operations:
        // 1. Read count
        // 2. Add 1
        // 3. Write back to count
    }
    
    public int getCount() {
        return count;
    }
}

// Problem: Multiple threads can interfere with each other
// Result: count might be less than expected
```

### Solution: Synchronized Methods
```java
class SafeCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++; // Now thread-safe!
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// How it works:
// - Only one thread can execute synchronized method at a time
// - Other threads wait until the lock is released
```

### Synchronized Blocks (More Granular Control)
```java
class BetterCounter {
    private int count = 0;
    private final Object lock = new Object();
    
    public void increment() {
        synchronized (lock) {
            count++; // Only this part is synchronized
        }
        // Other code can run in parallel
        doSomeOtherWork();
    }
    
    private void doSomeOtherWork() {
        // This doesn't need synchronization
    }
}
```

## 3. Producer-Consumer Pattern

**Classic concurrency problem:** Producer creates data, Consumer processes it. They share a buffer.

```java
import java.util.LinkedList;
import java.util.Queue;

class Buffer {
    private Queue<Integer> queue = new LinkedList<>();
    private final int capacity = 5;
    
    public synchronized void produce(int item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait(); // Wait if buffer is full
        }
        queue.offer(item);
        System.out.println("Produced: " + item + " [Buffer size: " + queue.size() + "]");
        notifyAll(); // Wake up all waiting consumers
    }
    
    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(); // Wait if buffer is empty
        }
        int item = queue.poll();
        System.out.println("Consumed: " + item + " [Buffer size: " + queue.size() + "]");
        notifyAll(); // Wake up all waiting producers
        return item;
    }
}

class Producer implements Runnable {
    private Buffer buffer;
    
    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }
    
    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                buffer.produce(i);
                Thread.sleep(100); // Simulate production time
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private Buffer buffer;
    
    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }
    
    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                buffer.consume();
                Thread.sleep(150); // Simulate consumption time
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Usage:
// Buffer buffer = new Buffer();
// Thread producer = new Thread(new Producer(buffer));
// Thread consumer = new Thread(new Consumer(buffer));
// producer.start();
// consumer.start();
```

**Key Concepts:**
- `wait()`: Releases lock and waits for notification
- `notify()`/`notifyAll()`: Wakes up waiting threads
- `while` loop: Check condition again after waking up (spurious wakeups)

## 4. Thread Pools

**Problem with creating individual threads:**
- Thread creation is expensive
- Too many threads can overwhelm system
- No control over resource usage

**Solution: Thread Pools**

```java
import java.util.concurrent.*;

class TaskRunner {
    public static void main(String[] args) {
        // Create thread pool with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Submit tasks to the pool
        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " running on " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(1000); // Simulate work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Task " + taskId + " completed");
            });
        }
        
        executor.shutdown(); // Important: shutdown the executor
        
        try {
            // Wait for all tasks to complete
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
```

**Types of Thread Pools:**
```java
// Fixed number of threads
ExecutorService fixed = Executors.newFixedThreadPool(4);

// Single thread for sequential execution
ExecutorService single = Executors.newSingleThreadExecutor();

// Creates new threads as needed
ExecutorService cached = Executors.newCachedThreadPool();

// For scheduled tasks
ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
scheduled.scheduleAtFixedRate(() -> System.out.println("Heartbeat"), 
    0, 5, TimeUnit.SECONDS);
```

## 5. CompletableFuture

**Modern way to handle asynchronous programming in Java.**

### Basic Usage
```java
import java.util.concurrent.CompletableFuture;

class AsyncExample {
    public static void main(String[] args) {
        // Create async task
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000); // Simulate long-running task
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Hello from async world!";
        });
        
        // Non-blocking: continue with other work
        System.out.println("Main thread continues...");
        
        // Get result when ready
        future.thenAccept(result -> {
            System.out.println("Received: " + result);
        });
        
        // Block and wait for completion (if needed)
        String result = future.join();
        System.out.println("Final result: " + result);
    }
}
```

### Chaining Operations
```java
CompletableFuture<String> result = CompletableFuture
    .supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World")           // Transform result
    .thenApply(String::toUpperCase)         // Transform again
    .thenCompose(s -> CompletableFuture     // Chain another async operation
        .supplyAsync(() -> s + "!"));

System.out.println(result.join()); // "HELLO WORLD!"
```

### Combining Multiple Futures
```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
    sleep(1000);
    return "Hello";
});

CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    sleep(1500);
    return "World";
});

// Wait for both to complete
CompletableFuture<String> combined = future1.thenCombine(future2, 
    (s1, s2) -> s1 + " " + s2);

// Wait for first to complete
CompletableFuture<String> fastest = future1.applyToEither(future2, 
    s -> "First: " + s);

// Wait for all to complete
CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2);
allOf.thenRun(() -> System.out.println("All completed!"));
```

### Exception Handling
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> {
        if (Math.random() > 0.5) {
            throw new RuntimeException("Random failure!");
        }
        return "Success!";
    })
    .handle((result, exception) -> {
        if (exception != null) {
            return "Error handled: " + exception.getMessage();
        }
        return result;
    })
    .exceptionally(throwable -> "Fallback value");
```

## 6. Real-World Example: Download Manager

```java
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

class DownloadManager {
    private ExecutorService executor = Executors.newFixedThreadPool(4);
    
    public CompletableFuture<List<String>> downloadFiles(List<String> urls) {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        // Start all downloads in parallel
        for (String url : urls) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                return downloadFile(url);
            }, executor);
            futures.add(future);
        }
        
        // Combine all results
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()))
            .whenComplete((result, exception) -> {
                if (exception == null) {
                    System.out.println("All downloads completed successfully!");
                } else {
                    System.out.println("Some downloads failed: " + exception.getMessage());
                }
                executor.shutdown();
            });
    }
    
    private String downloadFile(String url) {
        try {
            System.out.println("Starting download: " + url);
            Thread.sleep(2000 + (int)(Math.random() * 3000)); // Simulate variable download time
            System.out.println("Completed download: " + url);
            return "Downloaded: " + url;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Failed: " + url;
        }
    }
}

// Usage:
// DownloadManager manager = new DownloadManager();
// List<String> urls = Arrays.asList("file1.zip", "file2.pdf", "file3.doc");
// manager.downloadFiles(urls).thenAccept(results -> {
//     results.forEach(System.out::println);
// });
```

## 7. Locking Mechanisms

### synchronized vs ReentrantLock

**Traditional synchronized:**
```java
class BankAccount {
    private double balance = 1000.0;
    
    public synchronized void withdraw(double amount) {
        if (balance >= amount) {
            System.out.println("Withdrawing: " + amount);
            balance -= amount;
            System.out.println("New balance: " + balance);
        } else {
            System.out.println("Insufficient funds!");
        }
    }
    
    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited: " + amount + ", Balance: " + balance);
    }
}
```

**ReentrantLock (More Flexible):**
```java
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

class AdvancedBankAccount {
    private double balance = 1000.0;
    private final ReentrantLock lock = new ReentrantLock();
    
    public void withdraw(double amount) {
        lock.lock(); // Acquire lock
        try {
            if (balance >= amount) {
                System.out.println("Withdrawing: " + amount);
                balance -= amount;
                System.out.println("New balance: " + balance);
            } else {
                System.out.println("Insufficient funds!");
            }
        } finally {
            lock.unlock(); // ALWAYS unlock in finally block!
        }
    }
    
    public boolean tryWithdraw(double amount) {
        try {
            // Try to get lock for 2 seconds
            if (lock.tryLock(2, TimeUnit.SECONDS)) {
                try {
                    if (balance >= amount) {
                        balance -= amount;
                        System.out.println("Withdraw successful: " + amount);
                        return true;
                    }
                    System.out.println("Insufficient funds!");
                    return false;
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println("Could not acquire lock - timeout!");
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public void interruptibleWithdraw(double amount) throws InterruptedException {
        lock.lockInterruptibly(); // Can be interrupted
        try {
            // Withdrawal logic here
        } finally {
            lock.unlock();
        }
    }
}
```

**When to use ReentrantLock:**
- Need timeout for lock acquisition
- Need to interrupt thread waiting for lock
- Need fair lock (first-come-first-served)
- Need to check if lock is available without blocking

## 8. ReadWriteLock

**Problem:** Many threads read data, few threads write. Synchronized blocks all readers when one is reading.

**Solution:** ReadWriteLock allows multiple readers or single writer.

```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;
import java.util.Map;

class ThreadSafeCache {
    private final Map<String, String> cache = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Multiple threads can read simultaneously
    public String get(String key) {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " reading: " + key);
            Thread.sleep(100); // Simulate read time
            return cache.get(key);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // Only one thread can write at a time
    public void put(String key, String value) {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " writing: " + key + "=" + value);
            Thread.sleep(200); // Simulate write time
            cache.put(key, value);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
            System.out.println("Cache cleared");
        } finally {
            lock.writeLock().unlock();
        }
    }
}

// Benefits:
// - Multiple readers can read concurrently
// - Writers get exclusive access
// - Better performance for read-heavy workloads
```

## 9. Atomic Variables

**Lock-free programming for simple operations.**

### Basic Atomic Operations
```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class AtomicCounter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet(); // Thread-safe without locks!
    }
    
    public void add(int value) {
        count.addAndGet(value);
    }
    
    public int get() {
        return count.get();
    }
    
    // Compare and swap operation
    public boolean setIfExpected(int expected, int newValue) {
        return count.compareAndSet(expected, newValue);
    }
    
    public int incrementAndGetPrevious() {
        return count.getAndIncrement(); // Returns old value, then increments
    }
}
```

### Lock-Free Stack Implementation
```java
import java.util.concurrent.atomic.AtomicReference;

class AtomicStack<T> {
    private final AtomicReference<Node<T>> head = new AtomicReference<>();
    
    private static class Node<T> {
        final T data;
        Node<T> next;
        
        Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }
    
    public void push(T item) {
        Node<T> newNode = new Node<>(item, null);
        Node<T> currentHead;
        
        do {
            currentHead = head.get();
            newNode.next = currentHead;
        } while (!head.compareAndSet(currentHead, newNode));
        // Retry until successful (lock-free)
    }
    
    public T pop() {
        Node<T> currentHead;
        Node<T> newHead;
        
        do {
            currentHead = head.get();
            if (currentHead == null) {
                return null; // Stack is empty
            }
            newHead = currentHead.next;
        } while (!head.compareAndSet(currentHead, newHead));
        // Retry until successful
        
        return currentHead.data;
    }
}
```

**Advantages of Atomic Variables:**
- No locks needed
- No blocking
- Better performance for simple operations
- No deadlock possibility

**Disadvantages:**
- Limited to simple operations
- Can have ABA problem in complex scenarios
- More complex logic for compound operations

## 10. Deadlock Prevention

### Deadlock Example (DON'T DO THIS)
```java
class DeadlockExample {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    public void method1() {
        synchronized (lock1) {
            System.out.println("Thread 1: Holding lock1...");
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            
            System.out.println("Thread 1: Waiting for lock2...");
            synchronized (lock2) { // Waiting for lock2
                System.out.println("Thread 1: Holding lock1 & lock2...");
            }
        }
    }
    
    public void method2() {
        synchronized (lock2) {
            System.out.println("Thread 2: Holding lock2...");
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            
            System.out.println("Thread 2: Waiting for lock1...");
            synchronized (lock1) { // Waiting for lock1 - DEADLOCK!
                System.out.println("Thread 2: Holding lock1 & lock2...");
            }
        }
    }
}
```

### Solution 1: Ordered Locking
```java
class DeadlockPrevention {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    public void method1() {
        synchronized (lock1) { // Always acquire locks in same order
            synchronized (lock2) {
                System.out.println("Method 1: Both locks acquired safely");
            }
        }
    }
    
    public void method2() {
        synchronized (lock1) { // Same order prevents deadlock
            synchronized (lock2) {
                System.out.println("Method 2: Both locks acquired safely");
            }
        }
    }
}
```

### Solution 2: Timeout with ReentrantLock
```java
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

class TimeoutDeadlockPrevention {
    private final ReentrantLock lock1 = new ReentrantLock();
    private final ReentrantLock lock2 = new ReentrantLock();
    
    public void method1() {
        try {
            if (lock1.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    if (lock2.tryLock(1, TimeUnit.SECONDS)) {
                        try {
                            System.out.println("Method 1: Both locks acquired");
                        } finally {
                            lock2.unlock();
                        }
                    } else {
                        System.out.println("Method 1: Couldn't get lock2, avoiding deadlock");
                    }
                } finally {
                    lock1.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

## 11. Condition Variables

**More sophisticated waiting mechanism than wait/notify.**

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.util.Queue;

class BoundedBuffer<T> {
    private final Queue<T> buffer = new LinkedList<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();   // Producers wait on this
    private final Condition notEmpty = lock.newCondition();  // Consumers wait on this
    
    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }
    
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (buffer.size() == capacity) {
                System.out.println("Buffer full, producer waiting...");
                notFull.await(); // Wait until buffer is not full
            }
            buffer.offer(item);
            System.out.println("Produced: " + item + " [Size: " + buffer.size() + "]");
            notEmpty.signal(); // Signal waiting consumers
        } finally {
            lock.unlock();
        }
    }
    
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (buffer.isEmpty()) {
                System.out.println("Buffer empty, consumer waiting...");
                notEmpty.await(); // Wait until buffer is not empty
            }
            T item = buffer.poll();
            System.out.println("Consumed: " + item + " [Size: " + buffer.size() + "]");
            notFull.signal(); // Signal waiting producers
            return item;
        } finally {
            lock.unlock();
        }
    }
    
    public int size() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }
}

// Usage:
// BoundedBuffer<String> buffer = new BoundedBuffer<>(3);
// 
// // Producer thread
// new Thread(() -> {
//     try {
//         for (int i = 1; i <= 10; i++) {
//             buffer.put("Item-" + i);
//             Thread.sleep(200);
//         }
//     } catch (InterruptedException e) {
//         Thread.currentThread().interrupt();
//     }
// }).start();
// 
// // Consumer thread
// new Thread(() -> {
//     try {
//         for (int i = 1; i <= 10; i++) {
//             String item = buffer.take();
//             Thread.sleep(500);
//         }
//     } catch (InterruptedException e) {
//         Thread.currentThread().interrupt();
//     }
// }).start();
```

**Advantages over wait/notify:**
- More precise control (separate conditions)
- Can have multiple conditions
- Better performance (no thundering herd)
- More readable code

## 12. Thread-Safe Singleton

### Double-Checked Locking Pattern
```java
class ThreadSafeSingleton {
    private static volatile ThreadSafeSingleton instance;
    
    private ThreadSafeSingleton() {
        // Simulate expensive initialization
        try {
            Thread.sleep(1000);
            System.out.println("Singleton initialized by " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static ThreadSafeSingleton getInstance() {
        if (instance == null) { // First check (no locking)
            synchronized (ThreadSafeSingleton.class) {
                if (instance == null) { // Second check (with locking)
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }
    
    public void doSomething() {
        System.out.println("Doing something...");
    }
}

// Test with multiple threads:
// for (int i = 0; i < 10; i++) {
//     new Thread(() -> {
//         ThreadSafeSingleton singleton = ThreadSafeSingleton.getInstance();
//         singleton.doSomething();
//     }, "Thread-" + i).start();
// }
```

**Why volatile is needed:**
- Prevents reordering of instructions
- Ensures visibility across threads
- Without volatile, other threads might see partially constructed object

### Enum Singleton (Recommended)
```java
enum SingletonEnum {
    INSTANCE;
    
    public void doSomething() {
        System.out.println("Doing something with enum singleton");
    }
}

// Usage: SingletonEnum.INSTANCE.doSomething();
// - Thread-safe by default
// - Prevents multiple instantiation
// - Handles serialization correctly
```

## Lock Comparison Table

| Lock Type | Use Case | Pros | Cons | Example |
|-----------|----------|------|------|---------|
| **synchronized** | Simple mutual exclusion | Easy, built-in, automatic cleanup | Not interruptible, no timeout | Bank account operations |
| **ReentrantLock** | Advanced locking needs | Interruptible, timeout, fair queuing | Manual unlock required | Resource allocation with timeout |
| **ReadWriteLock** | Read-heavy scenarios | Multiple readers allowed | Write starvation possible | Cache implementations |
| **Atomic Variables** | Simple operations | Lock-free, high performance | Limited operations only | Counters, flags |
| **Condition Variables** | Complex waiting scenarios | Precise control, multiple conditions | More complex setup | Producer-consumer with multiple conditions |

## Interview Quick Reference

### Common Questions & Answers

**Q: What's the difference between start() and run()?**
A: `start()` creates a new thread and calls `run()` in it. `run()` executes in the current thread.

**Q: What is the volatile keyword?**
A: Ensures variable is read from main memory, not CPU cache. Prevents visibility issues between threads.

**Q: Explain wait() vs sleep()?**
A: `wait()` releases the lock and waits for notification. `sleep()` just pauses the thread without releasing locks.

**Q: What causes deadlock?**
A: Circular dependency of locks. Thread A waits for Thread B's lock while B waits for A's lock.

**Q: How to prevent deadlock?**
A: 1) Always acquire locks in the same order, 2) Use timeouts, 3) Avoid nested locks when possible.

**Q: When to use thread pools?**
A: When you have many short-lived tasks. Avoids overhead of creating/destroying threads.

**Q: What is the happens-before relationship?**
A: Guarantees that memory writes by one thread are visible to another thread. Established by synchronization primitives.

**Q: Difference between Callable and Runnable?**
A: `Callable` can return a value and throw checked exceptions. `Runnable` cannot.

### Performance Tips

1. **Use thread pools** instead of creating individual threads
2. **Minimize synchronization scope** - synchronize only what's necessary
3. **Prefer atomic variables** for simple operations
4. **Use ReadWriteLock** for read-heavy scenarios
5. **Avoid nested locks** to prevent deadlocks
6. **Use CompletableFuture** for async programming
7. **Profile your application** to find bottlenecks

### Modern Java JavaConcepts.Concurrency (Java 19+)

```java
// Virtual Threads (Project Loom)
Thread.ofVirtual().start(() -> {
    // Lightweight thread - can create millions!
});

// Structured JavaConcepts.Concurrency
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> task1 = scope.fork(() -> fetchUser());
    Future<String> task2 = scope.fork(() -> fetchData());
    
    scope.join();           // Wait for all tasks
    scope.throwIfFailed();  // Propagate exceptions
    
    // Use results
    String user = task1.resultNow();
    String data = task2.resultNow();
}
```

This comprehensive guide covers all essential multithreading concepts for Java interviews and real-world development!