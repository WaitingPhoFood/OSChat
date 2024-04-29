package server;

import room.Room;

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

    public SocketServerThread(Socket socket, ArrayList<SocketServerThread> threads, ArrayList<Room> rooms) {
        this.socket = socket;
        this.threads = threads;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error setting up streams: " + e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                String message = (String) input.readObject();
                broadcast(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error or connection closed: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void broadcast(String message) {
        synchronized (threads) {
            for (SocketServerThread thread : threads) {
                if (thread != this) {
                    try {
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
}


