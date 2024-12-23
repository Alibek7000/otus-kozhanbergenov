package kz.threadpull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPull {

    private final List<Runnable> taskQueue = new LinkedList<>();
    private Semaphore semaphore;
    private AtomicBoolean enabled = new AtomicBoolean(true);
    private final List<Thread> workers = new LinkedList<>();

    public ThreadPull(int numberOfThreads) {
        runThreads(numberOfThreads);
    }

    private void runThreads(int numberOfThreads) {
        semaphore = new Semaphore(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            Thread worker = new Thread(() -> {
                while (enabled.get() || !taskQueue.isEmpty()) {
                    Runnable task = null;
                    synchronized (taskQueue) {
                        if (!taskQueue.isEmpty()) {
                            task = taskQueue.removeFirst();
                        } else if (!enabled.get()) {
                            return;
                        }
                    }

                    if (task != null) {
                        try {
                            semaphore.acquire();
                            task.run();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.out.println(Thread.currentThread().getName() + " interrupted");
                        } finally {
                            semaphore.release();
                        }
                    }

                }
            });
            worker.start();
            workers.add(worker);
        }
    }

    public synchronized void execute(Runnable runnable) {
        if (!enabled.get()) {
            throw new IllegalStateException("ThreadPool is shut down");
        }
        synchronized (taskQueue) {
            taskQueue.add(runnable);
        }
    }

    public synchronized void shutdown() {
        enabled.set(false);
        synchronized (taskQueue) {
            taskQueue.notifyAll(); // Освобождение заблокированных потоков
        }
        for (Thread worker : workers) {
            worker.interrupt();
        }

    }
}
