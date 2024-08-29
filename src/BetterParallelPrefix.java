public class BetterParallelPrefix implements PrefixInterface{
    public void run(String filename, int bufferSize) {

    }
    public static void main(String[] args) {
        new BetterParallelPrefix().run("in.txt", 1000);
    }
}
