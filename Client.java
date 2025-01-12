import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * La classe Client rappresenta un'applicazione client di chat che si connette a un server,
 * invia e riceve messaggi attraverso una GUI costruita con Swing.
 */
public class Client {
    private static JTextArea chatArea; // Area di testo per visualizzare la chat
    private static JTextField messageField; // Campo di testo per inserire messaggi
    private static JTextField serverField; // Campo di testo per l'indirizzo del server
    private static JTextField portField; // Campo di testo per la porta del server
    private static PrintWriter out; // Flusso di output per inviare messaggi al server
    private static String username; // Nome utente del client
    private static Socket socket; // Socket per la connessione al server
    private static JButton connectButton; // Bottone per la connessione al server

    /**
     * Metodo principale che avvia l'applicazione e crea l'interfaccia grafica.
     * @param args Argomenti della riga di comando (non utilizzati).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::createAndShowGUI);
    }

    /**
     * Crea e mostra l'interfaccia grafica dell'applicazione.
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Client Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        frame.setLayout(new BorderLayout());

        // Pannello superiore per l'indirizzo del server e la porta
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel serverLabel = new JLabel("Server:");
        serverField = new JTextField("10.0.75.21", 10);
        JLabel portLabel = new JLabel("Porta:");
        portField = new JTextField("12345", 5);

        connectButton = new JButton("Connetti");
        connectButton.addActionListener(e -> connectToServer());
        connectButton.setEnabled(true);

        topPanel.add(serverLabel);
        topPanel.add(serverField);
        topPanel.add(portLabel);
        topPanel.add(portField);
        topPanel.add(connectButton);

        frame.add(topPanel, BorderLayout.NORTH);

        // Area della chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Pannello inferiore per l'invio dei messaggi
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Invia");
        sendButton.addActionListener(e -> sendMessage());
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Stabilisce la connessione con il server utilizzando l'indirizzo e la porta specificati.
     */
    private static void connectToServer() {
        String serverAddress = serverField.getText();
        int port = Integer.parseInt(portField.getText());

        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String prompt = in.readLine(); // Legge il prompt del server per il nome utente
            username = JOptionPane.showInputDialog(prompt);
            out.println(username);

            connectButton.setEnabled(false);

            new Thread(new ReceiveHandler(socket)).start();
            chatArea.append("Connesso al server.\n");

        } catch (IOException e) {
            chatArea.append("Impossibile connettersi al server.\n");
            e.printStackTrace();
        }
    }

    /**
     * Invia un messaggio al server.
     */
    private static void sendMessage() {
        String message = messageField.getText();
        if (!message.trim().isEmpty()) {
            out.println(message);
            messageField.setText("");
        }
    }

    /**
     * Classe interna per gestire la ricezione dei messaggi dal server.
     */
    static class ReceiveHandler implements Runnable {
        private Socket socket;

        /**
         * Costruttore per inizializzare il gestore di ricezione con il socket specificato.
         * @param socket Il socket per la connessione al server.
         */
        public ReceiveHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Metodo eseguito dal thread per ricevere e visualizzare i messaggi dal server.
         */
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                chatArea.append("Connessione con il server persa.\n");
            }
        }
    }
}
