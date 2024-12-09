import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Connesso al server.");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            // Ricezione e invio del nome utente
            System.out.print(in.readLine()); // Server chiede il nome utente
            String username = scanner.nextLine();
            out.println(username);

            // Thread per ricevere i messaggi
            new Thread(new ReceiveHandler(socket, username)).start();

            // Loop per inviare i messaggi
            while (true) {
                System.out.print("Inserisci messaggio: ");
                String message = scanner.nextLine();
                out.println(message);
                System.out.println("tu: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ReceiveHandler implements Runnable {
    private Socket socket;
    private String username;

    public ReceiveHandler(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                // Non stampare "tu:" per i messaggi inviati dall'utente stesso
                if (!message.startsWith(username + ":")) {
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Connessione con il server persa.");
        }
    }
}
