import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientGUI extends Application {

    private PrintWriter out;
    private TextArea messageArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Client GUI");

        // UI components
        Label serverLabel = new Label("Server Address:");
        TextField serverInput = new TextField("127.0.0.1");
        Label portLabel = new Label("Port:");
        TextField portInput = new TextField("12345");
        Button connectButton = new Button("Connect");

        messageArea = new TextArea();
        messageArea.setEditable(false);

        TextField inputField = new TextField();
        inputField.setPromptText("Enter your message here");
        Button sendButton = new Button("Send");

        VBox layout = new VBox(10, serverLabel, serverInput, portLabel, portInput, connectButton, messageArea, inputField, sendButton);
        layout.setSpacing(10);

        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Event handlers
        connectButton.setOnAction(e -> {
            String serverAddress = serverInput.getText();
            int port = Integer.parseInt(portInput.getText());
            connectToServer(serverAddress, port);
        });

        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            if (out != null && !message.isEmpty()) {
                out.println(message);
                inputField.clear();
            }
        });
    }

    private void connectToServer(String serverAddress, int port) {
        new Thread(() -> {
            try (Socket socket = new Socket(serverAddress, port)) {
                messageArea.appendText("Connected to server\n");

                Scanner in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                while (in.hasNextLine()) {
                    String response = in.nextLine();
                    messageArea.appendText(response + "\n");
                }

            } catch (Exception e) {
                messageArea.appendText("Error: " + e.getMessage() + "\n");
            }
        }).start();
    }
}
