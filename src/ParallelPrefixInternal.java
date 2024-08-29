public class ParallelPrefixInternal implements PrefixInterface{
    public void run(String filename, int bufferSize) {

    }
    public static void main(String[] args) {
        new ParallelPrefixInternal().run("in.txt", 1000);
    }
}
