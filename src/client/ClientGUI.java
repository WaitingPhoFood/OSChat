package client;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.util.ArrayList;

import message.ChatMessage;
import message.ChatMessageType;

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
    private JLabel profilePictureLabel;
    private JPanel profilePanel;
    private ArrayList<JLabel> profilePictureLabels = new ArrayList<>();
    private ArrayList<String> addedUsernames = new ArrayList<>();

    private JButton sendFileButton;
    private JFileChooser fileChooser;

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

        // Profile Picture Panel ---------------------
        // Create a panel for the user's profile
        profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));

        // Add the current user's profile to the panel
        addUserProfile(username, profilePicture);

        // Add the profile panel to the frame
        add(profilePanel, BorderLayout.WEST);


        //---------------------------------------------

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        doc = chatArea.getStyledDocument();
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        messageField = new JTextField(30);
        sendButton = new JButton("Send");
        sendFileButton = new JButton("Send File");
        fileChooser = new JFileChooser();
        connectButton = new JButton("Connect");
        serverAddressField = new JTextField("localhost", 10);
        serverPortField = new JTextField("6000", 5);

        panel.add(messageField);
        panel.add(sendButton);
        panel.add(sendFileButton);
        panel.add(serverAddressField);
        panel.add(serverPortField);
        panel.add(connectButton);
        add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(this);
        connectButton.addActionListener(this);
        sendFileButton.addActionListener(this);


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
                    if (chatMessage.getType() == ChatMessageType.NEW_USER) {
                        // A new user has joined, so add their profile to the panel
                        SwingUtilities.invokeLater(() -> {
                            if (!addedUsernames.contains(chatMessage.getSenderName())) {
                                addUserProfile(chatMessage.getSenderName(), chatMessage.getUserImage());
                                addedUsernames.add(chatMessage.getSenderName());
                            }
                        });
                    } else {
                        String text = chatMessage.getSenderName() + ": " + chatMessage.getMessage() + "\n";
                        SwingUtilities.invokeLater(() -> {
                            try {
                                doc.insertString(doc.getLength(), text, null);
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } else if (message instanceof String) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            doc.insertString(doc.getLength(), (String) message + "\n", null);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch(IOException | ClassNotFoundException e){
            SwingUtilities.invokeLater(() -> {
                try {
                    doc.insertString(doc.getLength(), "Error or connection closed: " + e.getMessage() + "\n", null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
            connectToServer();
            try {
                // Send the username and profile picture as a ChatMessage when the user first connects
                output.writeObject(new ChatMessage(username, ChatMessageType.NEW_USER, profilePicture));
                output.flush();
            } catch (IOException ex) {
                try {
                    doc.insertString(doc.getLength(), "Error sending message: " + ex.getMessage() + "\n", null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        } else if (e.getSource() == sendButton) {
            try {
                String messageText = messageField.getText();
                // Send only the message text as a String
                output.writeObject(messageText);
                output.flush();
                messageField.setText("");
                // Display the client's message in their own chat area
                try {
                    doc.insertString(doc.getLength(), username + ": " + messageText + "\n", null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            } catch (IOException ex) {
                try {
                    doc.insertString(doc.getLength(), "Error sending message: " + ex.getMessage() + "\n", null);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        } else if (e.getSource() == sendFileButton) {
            int returnVal = fileChooser.showOpenDialog(ClientGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    // Read file as bytes
                    byte[] content = Files.readAllBytes(file.toPath());
                    String fileDirectory = "C:\\Users\\adam.long";
                    System.out.println("File length: " + content.length);
                    // Create a file message (assuming ChatMessage can handle file data)
                    ChatMessage fileMessage = new ChatMessage(username, ChatMessageType.FILE, content, file.getName(), fileDirectory);
                    output.writeObject(fileMessage);
                    output.flush();


                    SwingUtilities.invokeLater(() -> {
                        try {
                            doc.insertString(doc.getLength(), "You sent a file: " + file.getName() + " located at: " + fileDirectory + "\n", null);
                        } catch (BadLocationException ble) {
                            ble.printStackTrace();
                        }
                    });

                } catch (IOException ex) {
                    try {
                        doc.insertString(doc.getLength(), "Error sending file: " + ex.getMessage() + "\n", null);
                    } catch (BadLocationException ble) {
                        ble.printStackTrace();
                    }
                }
            }
        }
    }

    // Method to add a user's profile to the panel
    private void addUserProfile(String username, ImageIcon profilePicture){
        // Create a label for the user's profile picture
        JLabel profilePictureLabel = new JLabel();
        profilePictureLabel.setIcon(profilePicture);
        profilePictureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create a label for the user's username
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add the labels to the profile panel
        profilePanel.add(profilePictureLabel);
        profilePanel.add(usernameLabel);

        // Add the profile picture label to the list
        profilePictureLabels.add(profilePictureLabel);

        // Repaint the profile panel to reflect the changes
        profilePanel.revalidate();
        profilePanel.repaint();
    }


    public static void startWithoutUser() {
        SwingUtilities.invokeLater(() -> new ClientGUI(null, null));
    }

    public static void main(String[] args) {
        ClientGUI.startWithoutUser();
    }
}