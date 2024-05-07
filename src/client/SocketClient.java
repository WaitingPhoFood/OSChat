package client;

import message.ChatMessage;
import message.ChatMessageType;
import message.JoinRoomRequest;
import message.JoinRoomResponse;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SocketClient {
    private String name;
    private Socket socket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    private ClientListener listener;


    public interface ClientListener {
        void onMessageReceived(String message);
        void onConnectionFailed(String error);
        void onDisconnected();
    }

    public SocketClient(String name, ClientListener listener) {
        this.name = name;
        this.listener = listener;
    }

    public static void main(String[] args) {
        ClientGUI.startWithoutUser();
    }


    private void initiate() {
        try {
            // Server communication initialization code...
            listener.onMessageReceived("Connected to server successfully!");
        } catch (Exception e) {
            if (listener != null) {
                listener.onConnectionFailed("Error during communication: " + e.getMessage());
            }
        }
    }


    public String getName() {
        return name;
    }
}