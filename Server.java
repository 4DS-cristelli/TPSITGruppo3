import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;

/**
 * La classe Server rappresenta un server di chat che accetta connessioni dai client,
 * gestisce la trasmissione dei messaggi e mantiene uno storico dei messaggi.
 */
public class Server {
    private static final int PORT = 12345; // Porta su cui il server ascolta le connessioni
    private static List<ClientHandler> clients = new ArrayList<>(); // Lista dei client connessi
    private static List<String> messageHistory = new ArrayList<>(); // Storico dei messaggi inviati

    /**
     * Metodo principale che avvia il server e gestisce le connessioni dei client.
     * @param args Argomenti della riga di comando (non utilizzati).
     */
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

    /**
     * Invia un messaggio a tutti i client connessi e lo aggiunge allo storico dei messaggi.
     * @param message Il messaggio da trasmettere.
     * @param sender Il client che ha inviato il messaggio.
     */
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

    /**
     * Restituisce una copia dello storico dei messaggi.
     * @return Una lista dei messaggi inviati.
     */
    public static List<String> getMessageHistory() {
        return new ArrayList<>(messageHistory);
    }

    /**
     * Rimuove un client dalla lista dei client connessi.
     * @param clientHandler Il gestore del client da rimuovere.
     */
    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
