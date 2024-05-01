package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ServerGUI extends JFrame implements ActionListener {
    private JButton startButton;
    private JButton stopButton;
    private JTextArea logArea;
    private JTextField ipAddressField;
    private JTextField portField; // New field for port input
    private SocketServer server;

    public ServerGUI() {
        super("Chat Server Control");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        server = new SocketServer(20); // Initialize the server instance

        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        ipAddressField = new JTextField(15); // 15 columns wide
        portField = new JTextField(5); // New field for port input, 5 columns wide
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        controlPanel.add(new JLabel("IP Address:"));
        controlPanel.add(ipAddressField);
        controlPanel.add(new JLabel("Port:")); // New label for port input
        controlPanel.add(portField); // Add the new field to the panel
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            String ipAddress = ipAddressField.getText();
            int port = Integer.parseInt(portField.getText()); // Parse the port input
            new Thread(() -> {
                try {
                    server.initServer(ipAddress, port); // Initialize the server with the specified IP and port
                    server.listen();
                    logArea.append("Server started on IP " + ipAddress + " and port " + server.getServerSocket().getLocalPort() + "...\n");
                } catch (IOException ex) {
                    logArea.append("Error starting server: " + ex.getMessage() + "\n");
                }
            }).start();
        } else if (e.getSource() == stopButton) {
            new Thread(() -> {
                try {
                    server.killServer();
                    logArea.append("Server stopped.\n");
                } catch (IOException ex) {
                    logArea.append("Error stopping server: " + ex.getMessage() + "\n");
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(ServerGUI::new);
    }
}