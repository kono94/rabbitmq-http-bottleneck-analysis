public class Main {
    public static void main(String[] args) throws Exception {
        Publisher publisher = new Publisher(args[0].trim().split(" "));
        publisher.start();
    }
}
