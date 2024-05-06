package server;

import message.ChatMessage;
import message.ChatMessageType;
import room.Room;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

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

                    if (chatMessage.getType() == ChatMessageType.NEW_USER && isNewUser) {
                        username = chatMessage.getSenderName();
                        profilePicture = chatMessage.getUserImage();
                        notifyNewUser();
                        isNewUser = false;
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