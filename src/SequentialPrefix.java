import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SequentialPrefix implements PrefixInterface {

    private int readFileToBuffer(BufferedReader reader, int[] buffer, int bufferSize) throws IOException {

        String line;
        int index = 0;

        // Read up to bufferSize lines into the buffer
        while (index < bufferSize && (line = reader.readLine()) != null) {
            buffer[index] = Integer.parseInt(line.trim());
            index++;
        }

        return index;
    }

    private void writeBufferToFile(BufferedWriter writer, int[] buffer, int length) throws IOException {
        
        for (int i = 0; i < length; i++) {
            writer.write(String.valueOf(buffer[i]));
            writer.newLine();
        }
    }

    private int prefixSum(int prefixSum, int[] buffer, int length) {
        
        // Perform the prefix sum
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] += prefixSum;
            prefixSum = buffer[i];
        }

        // Return the last value in the buffer as the new prefixSum
        return prefixSum;
    }

    public void run(String filename, int bufferSize) {
        
        String output_file = "../out/" + this.getClass().getName() + ".txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename));
             BufferedWriter writer = new BufferedWriter(new FileWriter(output_file))) {

            int prefixSum = 0;
            int length    = 0;
                
            int[] buffer  = new int[bufferSize];

            while ((length = this.readFileToBuffer(reader, buffer, bufferSize)) != 0) {
                prefixSum = this.prefixSum(prefixSum, buffer, length);
                this.writeBufferToFile(writer, buffer, length);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        int bufferSize = 1000;
        String input_file  = "../in2.txt";

        SequentialPrefix sq = new SequentialPrefix();

        sq.run(input_file, bufferSize);
    }
}
