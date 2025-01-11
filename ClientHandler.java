import java.io.*;
import java.net.*;
import java.util.*;
class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            out.println("Inserisci il tuo nome utente:");
            username = in.readLine();
            System.out.println(username + " si è connesso.");
            Server.broadcast(username + " è entrato nella chat!", this);

            // Invia i messaggi salvati
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


    public void sendMessage(String message) {
        out.println(message);
    }
}