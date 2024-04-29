package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientGUI extends JFrame implements ActionListener {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton, connectButton;
    private JTextField serverAddressField, serverPortField;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ClientGUI() {
        setTitle("Chat Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        messageField = new JTextField(30);
        sendButton = new JButton("Send");
        connectButton = new JButton("Connect");
        serverAddressField = new JTextField("localhost", 10);
        serverPortField = new JTextField("6000", 5);

        panel.add(messageField);
        panel.add(sendButton);
        panel.add(serverAddressField);
        panel.add(serverPortField);
        panel.add(connectButton);
        add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(this);
        connectButton.addActionListener(this);

        setVisible(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket(serverAddressField.getText(), Integer.parseInt(serverPortField.getText()));
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            new Thread(this::listen).start();
        } catch (IOException e) {
            chatArea.append("Could not connect to server: " + e.getMessage() + "\n");
        }
    }

    private void listen() {
        try {
            while (true) {
                Object message = input.readObject();
                if (message instanceof String) {
                    SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            chatArea.append("Lost connection to server.\n");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
            connectToServer();
        } else if (e.getSource() == sendButton) {
            try {
                output.writeObject(messageField.getText());
                output.flush();
                messageField.setText("");
            } catch (IOException ex) {
                chatArea.append("Error sending message: " + ex.getMessage() + "\n");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}
