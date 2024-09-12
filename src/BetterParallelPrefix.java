import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class BetterParallelPrefix extends RecursiveAction implements PrefixInterface {

    private final int DValue;
    private final int KValue;
    private final boolean upsweep;

    private static int[] out;
    private static final int NUM_THREADS = 10;

    public BetterParallelPrefix() {
        this(0, 0, false);
    }

    public BetterParallelPrefix(int DValue, int KValue, boolean upsweep) {
        this.DValue = DValue;
        this.KValue = KValue;
        this.upsweep = upsweep;
    }

    // Log base 2 function
    public static int log2(int x) {
        return (int) (Math.log(x) / Math.log(2));
    }

    // Check if the number is a power of two
    static boolean isPowerOfTwo(int n) {
        return (int) (Math.ceil(Math.log(n) / Math.log(2))) == (int) (Math.floor(Math.log(n) / Math.log(2)));
    }

    @Override
    public void compute() {
        if (upsweep) {
            int temp = out[KValue + (int) Math.pow(2, DValue) - 1] + out[KValue + (int) Math.pow(2, DValue + 1) - 1];
            out[KValue + (int) Math.pow(2, DValue + 1) - 1] = temp;
        } else {
            int temp = out[KValue + (int) Math.pow(2, DValue) - 1];
            out[KValue + (int) Math.pow(2, DValue) - 1] = out[KValue + (int) Math.pow(2, DValue + 1) - 1];
            out[KValue + (int) Math.pow(2, DValue + 1) - 1] += temp;
        }
    }

    // Main run method for file-based execution
    public void run(String filename, int bufferSize) {
        int prev = 0;
        String output_file = "../out/" + this.getClass().getName() + ".txt";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output_file))) {

            ForkJoinPool pool = new ForkJoinPool(NUM_THREADS);
            int next = isPowerOfTwo(bufferSize) ? bufferSize : (int) Math.pow(2, Math.ceil(log2(bufferSize) + 1));
            int[] in = new int[bufferSize];
            out = new int[next];

            while (bufferedReader.ready()) {
                int count = readInput(bufferedReader, in);
                if (count > 0) {
                    processChunk(in, count, next, prev, pool);
                    prev = out[out.length - 1];
                    shiftAndWriteOutput(bufferedWriter, count);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch ( InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Read input data from the file and store it in the array
    private int readInput(BufferedReader reader, int[] inputBuffer) throws IOException {
        Arrays.fill(inputBuffer, 0);
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
    private void processChunk(int[] in, int count, int next, int prev, ForkJoinPool pool) throws InterruptedException {
        in[0] += prev;  // Add previous carry-over
        resetOutputArray(next);
        System.arraycopy(in, 0, out, 0, count);
        performUpSweep(next, pool);
        performDownSweep(next, pool);
    }

    // Reset the output array
    private void resetOutputArray(int size) {
        Arrays.fill(out, 0, size, 0);
    }

    // Perform up-sweep phase of the prefix sum
    private void performUpSweep(int size, ForkJoinPool pool) throws InterruptedException {
        for (int d = 0; d < log2(size); d++) {
            for (int k = 0; k < size; k += (int) Math.pow(2, d + 1)) {
                BetterParallelPrefix subtask = new BetterParallelPrefix(d, k, true);
                pool.invoke(subtask);
                subtask.join();
            }
        }
    }

    // Perform down-sweep phase of the prefix sum
    private void performDownSweep(int size, ForkJoinPool pool) throws InterruptedException {
        out[size - 1] = 0;
        for (int d = log2(size) - 1; d >= 0; d--) {
            for (int k = 0; k <= size - 1; k += (int) Math.pow(2, d + 1)) {
                BetterParallelPrefix subtask = new BetterParallelPrefix(d, k, false);
                pool.invoke(subtask);
                subtask.join();
            }
        }
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

    // Main method
    public static void main(String[] args) {

        int bufferSize = 1000;
        String input_file  = "../in2.txt";

        BetterParallelPrefix bp = new BetterParallelPrefix();
        
        bp.run(input_file, bufferSize);
    }
}
