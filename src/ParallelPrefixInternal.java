import java.io.*;
import java.util.*;
import java.util.function.IntBinaryOperator;

public class ParallelPrefixInternal implements PrefixInterface {

    public static long ExecutionTime;

    // Operator for parallel prefix sum
    static int operator(int a, int b) {
        return a + b;
    }

    // Main run method for file-based execution
    public void run(String filename, int bufferSize) {
        IntBinaryOperator intBinaryOperator = (a, b) -> a + b;
        String output_file = "../out/" + this.getClass().getName() + ".txt";
        
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output_file))) {

            int[] in = new int[bufferSize];
            int[] out = new int[bufferSize];
            int prev = 0;
            int count;
            String line;

            // Process the input file in chunks
            while ((line = bufferedReader.readLine()) != null) {
                count = readInput(bufferedReader, in, line, bufferSize);
                if (count > 0) {
                    processChunk(in, out, count, prev, intBinaryOperator);
                    prev = out[count - 1]; // Store the last element for the next chunk
                    writeOutput(bufferedWriter, out, count);
                }
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Read input data from the file into the array
    private int readInput(BufferedReader reader, int[] inputBuffer, String initialLine, int bufferSize) throws IOException {
        int count = 0;
        inputBuffer[count++] = Integer.parseInt(initialLine);
        for (int i = 1; i < bufferSize; i++) {
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

    // Process a chunk using Java's parallel prefix
    private void processChunk(int[] in, int[] out, int count, int prev, IntBinaryOperator operator) {
        out[0] = in[0] + prev;  // Add the previous carry-over to the first element
        System.arraycopy(in, 1, out, 1, count - 1);  // Copy the rest of the elements
        Arrays.parallelPrefix(out, 0, count, operator);  // Perform parallel prefix on the array
    }

    // Write the output to the file
    private void writeOutput(BufferedWriter writer, int[] output, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            writer.write(output[i] + "\n");
        }
        writer.flush();
    }

    // Main method
    public static void main(String[] args) {

        int bufferSize = 1000;
        String input_file  = "../in2.txt";

        ParallelPrefixInternal pi = new ParallelPrefixInternal();
        
        pi.run(input_file, bufferSize);
    }
}
