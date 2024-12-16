import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client {
    private static JTextArea chatArea;
    private static JTextField messageField;
    private static JTextField serverField;
    private static JTextField portField;
    private static PrintWriter out;
    private static String username;
    private static Socket socket;
    private static JButton connectButton; // Bottone per la connessione

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Creazione della finestra principale
        JFrame frame = new JFrame("Client Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        // Layout della finestra
        frame.setLayout(new BorderLayout());

        // Pannello per l'inserimento dell'indirizzo del server e della porta
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // FlowLayout per posizionare gli elementi orizzontalmente

        JLabel serverLabel = new JLabel("Server:");
        serverField = new JTextField("10.0.75.21", 10); // Campo server con dimensione
        JLabel portLabel = new JLabel("Porta:");
        portField = new JTextField("12345", 5); // Campo porta con dimensione

        connectButton = new JButton("Connetti");
        connectButton.addActionListener(e -> connectToServer()); // Azione del bottone
        connectButton.setEnabled(true); // Il bottone è attivo fino alla connessione

        // Aggiungi gli elementi al pannello
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

        // Pannello per inviare i messaggi
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Invia");
        sendButton.addActionListener(e -> sendMessage());
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Mostra la finestra
        frame.setVisible(true);
    }

    private static void connectToServer() {
        String serverAddress = serverField.getText();
        int port = Integer.parseInt(portField.getText());

        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Ricezione e invio del nome utente
            String prompt = in.readLine(); // Server chiede il nome utente
            username = JOptionPane.showInputDialog(prompt);
            out.println(username);

            // Disabilita il bottone di connessione dopo aver stabilito la connessione
            connectButton.setEnabled(false);

            // Thread per ricevere i messaggi
            new Thread(new ReceiveHandler(socket)).start();
            chatArea.append("Connesso al server.\n");

        } catch (IOException e) {
            chatArea.append("Impossibile connettersi al server.\n");
            e.printStackTrace();
        }
    }

    private static void sendMessage() {
        String message = messageField.getText();
        if (!message.trim().isEmpty()) {
            out.println(message);
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
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                chatArea.append("Connessione con il server persa.\n");
            }
        }
    }
}
