package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Start extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JButton uploadButton;
    private JLabel pictureLabel;

    public Start() {
        super("Start");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        usernameField = new JTextField(20);
        uploadButton = new JButton("Upload Profile Picture");
        pictureLabel = new JLabel();

        uploadButton.addActionListener(this);

        add(new JLabel("Username:"));
        add(usernameField);
        add(uploadButton);
        add(pictureLabel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadButton) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // Read the image file
                    BufferedImage originalImage = ImageIO.read(selectedFile);
                    // Resize the image
                    Image resizedImage = originalImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    // Set the resized image as the icon of the JLabel
                    pictureLabel.setIcon(new ImageIcon(resizedImage));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Start::new);
    }
}
