import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Hemlo inside Docker conteener");
            Publisher pub = new Publisher(args[0].trim().split(" "));
            pub.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
