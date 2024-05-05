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

    public void connectToServer(String serverAddress, int serverPort) throws IOException {
        try {
            socket = new Socket(serverAddress, serverPort);
            clientOutput = new ObjectOutputStream(socket.getOutputStream());
            clientInput = new ObjectInputStream(socket.getInputStream());
            new Thread(this::initiate).start();
        } catch (IOException e) {
            if (listener != null) {
                listener.onConnectionFailed("Could not connect to server: " + e.getMessage());
            }
            throw e;
        }
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

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (listener != null) {
                listener.onDisconnected();
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onConnectionFailed("Error disconnecting: " + e.getMessage());
            }
        }
    }

    public void sendToServer(Object message) throws IOException {
        clientOutput.writeObject(message);
        clientOutput.flush();
    }

    public Object getServerReply() throws IOException, ClassNotFoundException {
        return clientInput.readObject();
    }

    public String getName() {
        return name;
    }
}