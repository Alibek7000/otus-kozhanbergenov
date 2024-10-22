package kz;

import kz.threadpull.ThreadPull;

public class Main {

    public static void main(String[] args) {
        ThreadPull threadPull = new ThreadPull(5);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            threadPull.execute(() -> {
                String threadName = Thread.currentThread().getName();
                long passedFromStart = System.currentTimeMillis() - start;
                System.out.println(threadName + ". Task number " + finalI + " runs his job. " + passedFromStart + " ms passed from start");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        threadPull.shutdown();

    }
}