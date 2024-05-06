package Main;

import client.ClientGUI;
import server.ServerGUI;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;

public class Start extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JButton uploadButton;
    private JButton clearPFPButton; // New button
    private JButton nextButton;
    private JButton createServerButton;
    private JButton joinChatroomButton;
    private JLabel pictureLabel;
    private JLabel titleLabel;
    private ImageIcon userImage; //PFP

    public Start() {
        super("Start");
        setSize(720, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set the background color to the specified hex color
        getContentPane().setBackground(Color.decode("#ebc1c1"));
        Color backgroundColor = Color.decode("#ebc1c1");
        getContentPane().setBackground(backgroundColor);

        titleLabel = new JLabel("Welcome to SocketChat!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setOpaque(true); // Needed for JLabels
        titleLabel.setBackground(backgroundColor);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(backgroundColor); // Set the background color of the centerPanel

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(backgroundColor); // Set the background color of the topPanel

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(backgroundColor); // Set the background color of the bottomPanel

        usernameField = new JTextField(20);
        uploadButton = new JButton("Upload Profile Picture");
        clearPFPButton = new JButton("Clear Profile Picture"); // Initialize the button
        nextButton = new JButton("Next");
        createServerButton = new JButton("Create Server");
        joinChatroomButton = new JButton("Join Chatroom");
        pictureLabel = new JLabel();

        uploadButton.addActionListener(this);
        clearPFPButton.addActionListener(this); // Add action listener to the button
        nextButton.addActionListener(this);

        createServerButton.addActionListener(e -> new ServerGUI(usernameField.getText(), userImage)); // Add action listener to create server button
        joinChatroomButton.addActionListener(e -> new ClientGUI(usernameField.getText(), userImage)); // Add action listener to join chatroom button

        topPanel.add(new JLabel("Username:"));
        topPanel.add(usernameField);
        topPanel.add(uploadButton);
        topPanel.add(pictureLabel);
        topPanel.add(clearPFPButton); // Add the button to the panel
        topPanel.add(nextButton);

        // Get your IP address
        try {
            String ipAddress = getLocalIPv4Address();
            if (ipAddress != null) {
                JLabel ipLabel = new JLabel("Your IP Address: " + ipAddress + " Recommended Port is 6000");
                ipLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                topPanel.add(ipLabel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        centerPanel.add(topPanel);
        centerPanel.add(bottomPanel);
        centerPanel.add(Box.createVerticalGlue()); // Add more vertical glue
        centerPanel.add(Box.createVerticalGlue()); // Add more vertical glue
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createVerticalGlue());

        add(titleLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }
    //Not used, using the local IP address instead
    private String getPublicIPv4Address() throws IOException {
        URL url = new URL("http://checkip.amazonaws.com");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        return br.readLine();
    }

    private String getLocalIPv4Address() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadButton) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage originalImage = ImageIO.read(selectedFile);
                    Image resizedImage = originalImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    userImage = new ImageIcon(resizedImage);
                    pictureLabel.setIcon(userImage);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() == clearPFPButton) {
            userImage = null;
            pictureLabel.setIcon(null);
        } else if (e.getSource() == nextButton) {
            String username = usernameField.getText();
            if (username.isEmpty() || userImage == null) {
                JOptionPane.showMessageDialog(this, "Please enter a username and upload a profile picture.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JPanel bottomPanel = (JPanel) ((JPanel) getContentPane().getComponent(1)).getComponent(1);
                bottomPanel.add(createServerButton);
                bottomPanel.add(joinChatroomButton);
                bottomPanel.revalidate();
                bottomPanel.repaint();
            }
        } else if (e.getSource() == joinChatroomButton) {
            new ClientGUI(usernameField.getText(), userImage); // Pass the username and profile picture to the ClientGUI
        } else if (e.getSource() == createServerButton) {
            new ServerGUI(usernameField.getText(), userImage); // Pass the username and profile picture to the ServerGUI
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Start::new);
    }
}