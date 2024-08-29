public class ParallelPrefix implements PrefixInterface{
    public void run(String filename, int bufferSize) {

    }
    public static void main(String[] args) {
        new ParallelPrefix().run("in.txt", 1000);
    }
}
