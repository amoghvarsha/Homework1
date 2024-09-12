import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Benchmark {

    /* The input file we would like to use */
    public static String inputFileName = "../in.txt";

    public static String statsFile = "../stats/benchmark_results.txt";

    /* Total number of elements to hold in memory at a time */
    public static int[] bufferSizes = {1000, 10000, 100000};

    public static PrefixInterface[] prefixSums = {
        new SequentialPrefix(), 
        new ParallelPrefix(), 
        new ParallelPrefixInternal(), 
        new BetterParallelPrefix()
    };

    /* Find the average of numRuns # of runs for each type of prefix sum */
    public static int numRuns = 10;

    public static void main(String[] args) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(statsFile))) {
            writer.write("PrefixType,BufferSize,AverageTime(ms),Speedup\n"); // Write header

            System.out.printf("Running benchmarks...\n");

            double[] seqTime = new double[bufferSizes.length];  // To store SequentialPrefix execution times in ms

            /* Run for various buffer sizes  */
            for (int i = 0; i < bufferSizes.length; i++) {
                int bufferSize = bufferSizes[i];
                System.out.printf("Using buffer size of %d...\n\n", bufferSize);

                for (PrefixInterface s : prefixSums) {
                    System.out.printf("Running %s...\n", s.getClass().getName());

                    double execTime = 0;
                    double averageTime;
                    double speedup = 0;

                    for (int k = 0; k < numRuns; k++) {
                        long startTime = System.nanoTime();  // Use nanoTime for better accuracy
                        s.run(inputFileName, bufferSize);
                        long endTime = System.nanoTime();

                        // Convert to milliseconds when adding to execTime
                        execTime += (endTime - startTime) / 1_000_000.0;
                    }

                    averageTime = execTime / numRuns;  // Already in milliseconds

                    // Store SequentialPrefix time for speedup calculation
                    if (s instanceof SequentialPrefix) {
                        seqTime[i] = averageTime;
                        speedup = 1.0;  // Speedup is 1 for SequentialPrefix
                    } else {
                        speedup = seqTime[i] / averageTime;  // Calculate speedup
                    }

                    /* Write the result to the file */
                    writer.write(s.getClass().getSimpleName() + "," + bufferSize + "," + averageTime + "," + speedup + "\n");

                    /* Print results */
                    System.out.printf("Average time: %.3f ms\n", averageTime);  // Now in milliseconds
                    System.out.printf("Speedup: %.2f\n", speedup);
                    System.out.println("----------------------------------------------------------\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
