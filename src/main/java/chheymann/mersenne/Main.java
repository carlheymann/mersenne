package chheymann.mersenne;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Main class to start the Mersenne search. Runs checks concurrently over all cores, stopping every
 * 100 primes to print statistics
 */
public class Main {
    static final int STATS_BATCH_SIZE = 100;

    public static void main(String[] args) {
        PrimeSource primeSource = new PrimeSource(1000000);
        ExecutorService executor = Executors.newFixedThreadPool(getProcessorCount());
        Statistics stats = new Statistics();
        CountDownLatch latch = new CountDownLatch(STATS_BATCH_SIZE);
        int submittedJobs = 0;
        for (int prime : primeSource) {
            MersenneChecker checker = new MersenneChecker(prime, primeSource, stats, latch);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (checker.isPrime()) {
                        System.out.println("p = " + prime + " :) ");
                    }
                }
            });
            submittedJobs++;
            if (submittedJobs == STATS_BATCH_SIZE) {
                try {
                    latch.await();
                } catch (InterruptedException ie) {
                    System.exit(1);
                }
                System.out.println(stats);
                submittedJobs = 0;
                latch = new CountDownLatch(STATS_BATCH_SIZE);
                stats = new Statistics();
            }
        }
        waitForTermination(executor);
    }

    static int getProcessorCount() {
        int processors = Runtime.getRuntime().availableProcessors();
        if (processors == 0) {
            processors = 1;
        }
        return processors;
    }

    static void waitForTermination(ExecutorService executor) {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
        System.out.println("Shutting down..");
        executor.shutdownNow();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        System.out.println("Bye");
    }
}
