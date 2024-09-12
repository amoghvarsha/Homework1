import java.io.*;
import java.util.*;

public class ParallelPrefix implements PrefixInterface, Runnable {
    
    private final int DValue;
    private final int KValue;
    private static int[] out;
    private static boolean upSweep = true;

    public ParallelPrefix() {
        this(0, 0);
    }

    public ParallelPrefix(int DValue, int KValue) {
        this.DValue = DValue;
        this.KValue = KValue;
    }

    // Log base 2 function
    public static int log2(int x) {
        return (int) (Math.log(x) / Math.log(2));
    }

    // Check if the number is a power of two
    static boolean isPowerOfTwo(int n) {
        return (int) (Math.ceil(Math.log(n) / Math.log(2))) == (int) (Math.floor(Math.log(n) / Math.log(2)));
    }

    // Method to run the prefix sum algorithm on a file
    public void run(String filename, int bufferSize) {
        int prev = 0;
        String output_file = "../out/" + this.getClass().getName() + ".txt";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output_file))) {

            int next = isPowerOfTwo(bufferSize) ? bufferSize : (int) Math.pow(2, Math.ceil(log2(bufferSize) + 1));
            int[] in = new int[bufferSize];
            out = new int[next];

            while (bufferedReader.ready()) {
                int count = readInput(bufferedReader, in);
                if (count > 0) {
                    processChunk(in, count, next, prev);
                    prev = out[out.length - 1];  // Carry forward the last value
                    shiftAndWriteOutput(bufferedWriter, count);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Read input data from the file and store it in the array
    private int readInput(BufferedReader reader, int[] inputBuffer) throws IOException {
        Arrays.fill(inputBuffer, 0);  // Clear buffer
        int count = 0;
        for (int i = 0; i < inputBuffer.length; i++) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            try {
                inputBuffer[i] = Integer.parseInt(line);
                count++;
            } catch (NumberFormatException e) {
                break;
            }
        }
        return count;
    }

    // Process a chunk of input with the parallel prefix algorithm
    private void processChunk(int[] in, int count, int next, int prev) throws InterruptedException {
        in[0] += prev;  // Add previous carry-over
        resetOutputArray(next);
        System.arraycopy(in, 0, out, 0, count);
        performUpSweep(next);
        performDownSweep(next);
    }

    // Reset the output array
    private void resetOutputArray(int size) {
        Arrays.fill(out, 0, size, 0);
    }

    // Perform up-sweep phase of the prefix sum
    private void performUpSweep(int size) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        upSweep = true;
        for (int d = 0; d < log2(size); d++) {
            createThreads(threads, d, size);
            executeThreads(threads);
        }
    }

    // Perform down-sweep phase of the prefix sum
    private void performDownSweep(int size) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        upSweep = false;
        out[size - 1] = 0;  // Reset the last element
        for (int d = log2(size) - 1; d >= 0; d--) {
            createThreads(threads, d, size);
            executeThreads(threads);
        }
    }

    // Create threads for a given depth `d`
    private void createThreads(List<Thread> threads, int d, int size) {
        for (int k = 0; k < size; k += (int) Math.pow(2, d + 1)) {
            threads.add(new Thread(new ParallelPrefix(d, k), Integer.toString(d) + Integer.toString(k)));
        }
    }

    // Execute threads and wait for them to finish
    private void executeThreads(List<Thread> threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        threads.clear();
    }

    // Shift the output left and write it to the file
    private void shiftAndWriteOutput(BufferedWriter writer, int count) throws IOException {
        for (int i = 0; i < out.length - 1; i++) {
            out[i] = out[i + 1];
        }
        out[out.length - 1] = out[out.length - 1];  // Keep the last element as carry-over
        for (int i = 0; i < count; i++) {
            writer.write(out[i] + "\n");
        }
        writer.flush();
    }

    @Override
    public void run() {
        if (upSweep) {
            // Up-sweep operation
            int temp = out[KValue + (int) Math.pow(2, DValue) - 1] + out[KValue + (int) Math.pow(2, DValue + 1) - 1];
            out[KValue + (int) Math.pow(2, DValue + 1) - 1] = temp;
        } else {
            // Down-sweep operation
            int temp = out[KValue + (int) Math.pow(2, DValue) - 1];
            out[KValue + (int) Math.pow(2, DValue) - 1] = out[KValue + (int) Math.pow(2, DValue + 1) - 1];
            out[KValue + (int) Math.pow(2, DValue + 1) - 1] += temp;
        }
    }

    // Main method
    public static void main(String[] args) {

        int bufferSize = 1000;
        String input_file  = "../in2.txt";

        ParallelPrefix pp = new ParallelPrefix();

        pp.run(input_file, bufferSize);
    }
}
