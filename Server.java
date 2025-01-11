import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;

public class Server {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static List<String> messageHistory = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server avviato. In attesa di connessioni...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuovo client connesso: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        synchronized (messageHistory) {
            messageHistory.add(message);
            if (messageHistory.size() > 100) { // Limita il numero di messaggi salvati
                messageHistory.remove(0);
            }
        }

        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static List<String> getMessageHistory() {
        return new ArrayList<>(messageHistory);
    }

    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
