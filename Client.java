import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "10.0.75.21";
    private static final int SERVER_PORT = 12345;
    private static JTextArea chatArea;
    private static JTextField messageField;
    private static PrintWriter out;
    private static String username;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
        startClient();
    }

    private static void createAndShowGUI() {
        // Creazione della finestra principale
        JFrame frame = new JFrame("Client Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Layout della finestra
        frame.setLayout(new BorderLayout());

        // Area per i messaggi
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Campo per inviare i messaggi
        messageField = new JTextField();
        frame.add(messageField, BorderLayout.SOUTH);

        // Aggiungi un listener per il tasto Enter
        messageField.addActionListener(e -> sendMessage());

        // Mostra la finestra
        frame.setVisible(true);
    }

    private static void startClient() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Connesso al server.");
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            // Ricezione e invio del nome utente
            System.out.print(in.readLine()); // Server chiede il nome utente
            username = scanner.nextLine();
            out.println(username);

            // Thread per ricevere i messaggi
            new Thread(new ReceiveHandler(socket)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage() {
        String message = messageField.getText();
        if (!message.trim().isEmpty()) {
            out.println(message);
            chatArea.append("Tu: " + message + "\n");
            messageField.setText("");
        }
    }

    static class ReceiveHandler implements Runnable {
        private Socket socket;

        public ReceiveHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    // Non stampare i messaggi inviati dall'utente stesso
                    if (!message.startsWith(username + ":")) {
                        chatArea.append(message + "\n");
                    }
                }
            } catch (IOException e) {
                System.out.println("Connessione con il server persa.");
            }
        }
    }
}
