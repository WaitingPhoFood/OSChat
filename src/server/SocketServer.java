package server;

import room.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer {
    private ServerSocket serverSocket;
    private ArrayList<SocketServerThread> socketThreads;
    private ArrayList<Room> rooms;
    private int maxThreads;
    private boolean alive;

    public SocketServer(int maxThreads) {
        alive = true;
        socketThreads = new ArrayList<>();
        rooms = new ArrayList<>();
        this.maxThreads = maxThreads;
    }

    public void initServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server initialized on port " + port + " but not listening yet.");
    }

    public void listen() throws IOException {
        System.out.println("Server is now listening on port " + serverSocket.getLocalPort() + "\n");
        listenToClients();
    }

    private synchronized void listenToClients() throws IOException {
        Socket socket;
        while (alive) {
            System.out.println("Waiting for a client to connect");
            socket = serverSocket.accept();
            if (!alive) {
                break;
            }
            if (socketThreads.size() < maxThreads) {
                SocketServerThread newThread = new SocketServerThread(socket, socketThreads, rooms);
                socketThreads.add(newThread);
                new Thread(newThread).start();
                System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());
            } else {
                System.out.println("Max threads reached, connection refused");
                socket.close();
            }
        }
        for (SocketServerThread thread : socketThreads) {
            thread.killThread();
        }
    }

    public void killServer() throws IOException {
        alive = false;
        new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort()).close();
        serverSocket.close();
    }

    // Getter method for serverSocket
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public static void main(String[] args) {
        String ipAddress = "10.128.164.58"; // Specify the desired IP address here
        int port = 6000; // Specify the desired port here
        javax.swing.SwingUtilities.invokeLater(() -> new ServerGUI(ipAddress, port));
    }
}


