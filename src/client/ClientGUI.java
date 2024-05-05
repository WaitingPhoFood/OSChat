package client;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import message.ChatMessage;

public class ClientGUI extends JFrame implements ActionListener {
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton, connectButton;
    private JTextField serverAddressField, serverPortField;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;
    private ImageIcon profilePicture;
    private StyledDocument doc;

    public ClientGUI(String username, ImageIcon profilePicture) {
        setTitle("Chat Client");
        setSize(720, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        this.username = username;
        this.profilePicture = profilePicture;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (output != null) {
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ex) {
                    try {
                        doc.insertString(doc.getLength(), "Error disconnecting from server: " + ex.getMessage() + "\n", null);
                    } catch (BadLocationException ble) {
                        ble.printStackTrace();
                    }
                }
            }
        });

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        doc = chatArea.getStyledDocument();
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
            try {
                doc.insertString(doc.getLength(), "Could not connect to server: " + e.getMessage() + "\n", null);
            } catch (BadLocationException ble) {
                ble.printStackTrace();
            }
        }
    }

    private void listen() {
        try {
            while (true) {
                Object message = input.readObject();
                if (message instanceof ChatMessage) {
                    ChatMessage chatMessage = (ChatMessage) message;
                    String text = chatMessage.getSenderName() + ": " + chatMessage.getMessage() + "\n";
                    SwingUtilities.invokeLater(() -> {
                        try {
                            doc.insertString(doc.getLength(), text, null);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            try {
                doc.insertString(doc.getLength(), "Error or connection closed: " + e.getMessage() + "\n", null);
            } catch (BadLocationException ble) {
                ble.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
            connectToServer();
        } else if (e.getSource() == sendButton) {
            try {
                String messageText = messageField.getText();
                output.writeObject(messageText);
                output.flush();
                messageField.setText("");
            } catch (IOException ex) {
                try {
                    doc.insertString(doc.getLength(), "Error sending message: " + ex.getMessage() + "\n", null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        }
    }

    public static void startWithoutUser() {
        SwingUtilities.invokeLater(() -> new ClientGUI(null, null));
    }

    public static void main(String[] args) {
        ClientGUI.startWithoutUser();
    }
}