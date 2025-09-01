import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // converting the archive to a list
        List<String> lines = Files.readAllLines(Path.of("Entrada01.txt"), StandardCharsets.UTF_8);
        long[] numbers = new long[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            numbers[i] = Long.parseLong(lines.get(i).trim());
        }

        int[] threadCounts = {1, 5, 10, 16, 20};
        List<Long> exec_times = new ArrayList<>();

        // run the challenge for 1, 5 and 10 threads
        for (int tcount : threadCounts) {
            long exec_time = execute(numbers, tcount, "output");
            exec_times.add(exec_time);
        }

        // write the results on the output file
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of("output_times.csv"))) {
            bw.write("threads,ms\n");
            for (int i = 0; i < threadCounts.length; i++) {
                bw.write(threadCounts[i] + "," + exec_times.get(i) + "\n");
            }
        }
    }

    // execute the challenge
    private static long execute(long[] numbers, int nThreads, String prefix) throws Exception {
        Long[] results = new Long[numbers.length];
        Thread[] threads = new Thread[nThreads];
        int n = numbers.length;
        int block = (int) Math.ceil(n / (double) nThreads);

        long start_time = System.nanoTime();
        
        // establish each thread limits and instantiate it
        for (int t = 0; t < nThreads; t++) {
            int start = t * block;
            int end = Math.min(n, start + block);
            threads[t] = new Thread(
                new PrimeNumberVerifierThread(numbers, results, start, end));
            threads[t].start();
        }

        // wait for the thread to finish, since the numbers need to be in order
        for (Thread th : threads) th.join();

        // calculate the total time that took in milisseconds
        long end = System.nanoTime();
        long elapsedMs = (end - start_time) / 1000000;

        // write on the output file
        Path out = Path.of(prefix + "_T" + nThreads + ".txt");
        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            for (Long v : results) {
                if (v != null) {
                    bw.write(v.toString());
                    bw.newLine();
                }
            }
        }

        return elapsedMs;
    }
}

// class that implements the Runnable interface, meaning that 
// it can be executed in a thread
class PrimeNumberVerifierThread implements Runnable {
    private final long[] numbers;
    private final Long[] results;
    private final int start;
    private final int end;

    public PrimeNumberVerifierThread(long[] numbers, Long[] results, int start, int end) {
        this.numbers = numbers;
        this.results = results;
        this.start = start;
        this.end = end;
    }

    @Override
    // if the number is prime, add to the array
    public void run() {
        for (int i = start; i < end; i++) {
            if (PrimeNumberVerifier.isPrime(numbers[i])) results[i] = numbers[i];
        }
    }
}

class PrimeNumberVerifier {
    public static boolean isPrime(long number) {
        // verifies all the numbers until the root of it
        if (number <= 1) return false; 

        long limit = (long) Math.sqrt(number);
        for (long i = 2; i <= limit; i++) if (number % i == 0) return false;
        return true; 
    }
}