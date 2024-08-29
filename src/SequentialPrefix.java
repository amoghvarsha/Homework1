public class SequentialPrefix implements PrefixInterface{
    public void run(String filename, int bufferSize) {

    }
    public static void main(String[] args) {
        new SequentialPrefix().run("in.txt", 1000);
    }
}
