package server;

import message.ChatMessage;
import message.ChatMessageType;
import room.Room;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static message.ChatMessageType.FILE;

public class SocketServerThread implements Runnable {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private ArrayList<SocketServerThread> threads;
    private String username;
    private ImageIcon profilePicture;
    private boolean hasJoinedMessageSent = false;

    boolean isNewUser;

    public SocketServerThread(Socket socket, ArrayList<SocketServerThread> threads, ArrayList<Room> rooms) {
        this.socket = socket;
        this.threads = threads;
        this.username = username;
        this.profilePicture = profilePicture;
        this.isNewUser = true; // Initialize isNewUser to true

        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error setting up streams: " + e.getMessage());
        }
        // Notify all other clients that a new user has joined
        //notifyNewUser();
    }

    public void run() {
        try {
            while (true) {
                Object obj = input.readObject();

                if (obj instanceof ChatMessage) {
                    ChatMessage chatMessage = (ChatMessage) obj;

                    switch (chatMessage.getType()) {
                        case NEW_USER:
                            if (isNewUser) {
                                username = chatMessage.getSenderName();
                                profilePicture = chatMessage.getUserImage();
                                notifyNewUser();
                                isNewUser = false;
                            }
                            break;
                        case FILE:
                            // Handling file message
                            String receivedFileName = chatMessage.getFileName();  // Ensure getFileName() exists
                            byte[] fileData = chatMessage.getFileData();  // Ensure getFile() returns correct data
                            System.out.println("Received file: " + receivedFileName);
                            // Here you might save the file or update the GUI
                            saveFile(receivedFileName, fileData);
                            break;
                        default:
                            broadcast(chatMessage.getMessage());
                            break;
                    }
                } else if (obj instanceof String) {
                    String message = (String) obj;
                    broadcast(username + ": " + message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error or connection closed: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void saveFile(String fileName, byte[] data) {
        try {
            Path directoryPath = Paths.get("C:\\Users\\adam.long");
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);  // Create the directory if it doesn't exist
                System.out.println("Created directory: " + directoryPath);
            }

            Path filePath = directoryPath.resolve(fileName);
            Files.write(filePath, data);  // Write data to the file
            System.out.println("File saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Could not save file: " + e.getMessage());
            e.printStackTrace();
        }
    }






    //private void broadcast(String message) {
    //    synchronized (threads) {
    //        for (SocketServerThread thread : threads) {
    //            if (thread != this) {
    //                try {
    //                    thread.output.writeObject(message);
    //                    thread.output.flush();
    //                } catch (IOException e) {
    //                    System.out.println("Error broadcasting: " + e.getMessage());
    //                }
    //            }
    //        }
    //    }
    //}

    private void broadcast(String message) {
        synchronized (threads) {
            for (SocketServerThread thread : threads) {
                if (thread != this) {
                    try {
                        // Send a ChatMessage of type NEW_USER to all other clients
                        thread.output.writeObject(new ChatMessage(username, ChatMessageType.NEW_USER, profilePicture));
                        thread.output.flush();

                        // Send the actual message
                        thread.output.writeObject(message);
                        thread.output.flush();
                    } catch (IOException e) {
                        System.out.println("Error broadcasting: " + e.getMessage());
                    }
                }
            }
        }
    }

    private void closeConnection() {
        try {
            output.close();
            input.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
        threads.remove(this);
    }

    public void killThread() {
    }

    private void notifyNewUser() {
        synchronized (threads) {
            for (SocketServerThread thread : threads) {
                if (thread != this) {
                    try {
                        // Send a ChatMessage of type NEW_USER to all other clients
                        thread.output.writeObject(new ChatMessage(username, ChatMessageType.NEW_USER, profilePicture));
                        thread.output.flush();

                        // Send a String message to all other clients only once
                        if (!hasJoinedMessageSent) {
                            String message = username + " has joined the chatroom!";
                            thread.output.writeObject(message);
                            thread.output.flush();
                            hasJoinedMessageSent = true;
                        }
                    } catch (IOException e) {
                        System.out.println("Error notifying new user: " + e.getMessage());
                    }
                }
            }
        }
    }
}