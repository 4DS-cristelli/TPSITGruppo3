import java.io.*;
import java.net.*;
import java.util.*;

/**
 * La classe ClientHandler gestisce la comunicazione tra il server e un singolo client.
 * Implementa l'interfaccia Runnable per consentire l'esecuzione in un thread separato.
 */
class ClientHandler implements Runnable {
    private Socket socket; // Socket per la comunicazione con il client
    private BufferedReader in; // Flusso di input per ricevere dati dal client
    private PrintWriter out; // Flusso di output per inviare dati al client
    private String username; // Nome utente del client

    /**
     * Costruttore per inizializzare il gestore del client con il socket specificato.
     * @param socket Il socket per la connessione al client.
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo eseguito quando il thread inizia. Gestisce la comunicazione con il client.
     */
    @Override
    public void run() {
        try {
            out.println("Inserisci il tuo nome utente:");
            username = in.readLine();
            System.out.println(username + " si è connesso.");
            Server.broadcast(username + " è entrato nella chat!", this);

            // Invia i messaggi salvati al nuovo utente
            for (String message : Server.getMessageHistory()) {
                out.println(message);
            }

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(username + ": " + message);
                Server.broadcast(username + ": " + message, this);
            }
        } catch (IOException e) {
            System.out.println("Client disconnesso: " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.removeClient(this);
            Server.broadcast(username + " ha lasciato la chat.", this);
        }
    }

    /**
     * Invia un messaggio al client associato a questo gestore.
     * @param message Il messaggio da inviare.
     */
    public void sendMessage(String message) {
        out.println(message);
    }
}
