import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class SpamFilterServer {
    private static final int PORT = 5555;
    private static final int MAX_THREADS = 10;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

            System.out.println("SpamFilterServer is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}
