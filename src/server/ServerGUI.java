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
    private SocketServer server;

    public ServerGUI() {
        super("Chat Server Control");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
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
            new Thread(() -> {
                try {
                    if (server == null) {
                        server = new SocketServer(20);
                        server.initServer(6000);
                    }
                    server.listen();
                    logArea.append("Server started on port 6000...\n");
                } catch (IOException ex) {
                    logArea.append("Error starting server: " + ex.getMessage() + "\n");
                }
            }).start();
        } else if (e.getSource() == stopButton) {
            new Thread(() -> {
                try {
                    if (server != null) {
                        server.killServer();
                        logArea.append("Server stopped.\n");
                    }
                } catch (IOException ex) {
                    logArea.append("Error stopping server: " + ex.getMessage() + "\n");
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        new ServerGUI();
    }
}

