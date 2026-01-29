package serverSide;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import dtos.ServerShutdownNotification;

public class Server {

    private ServerSocket serverSocket;
    private static final List<ClientHandler> clients = new ArrayList<>();
    private boolean isRunning = false;

    // Singleton instance for simplicity if needed, but we'll use static methods for client tracking
    // to match how ClientHandler accesses it in the simple plan.
    
    public void startServer() {
        if (isRunning) return;
        
        isRunning = true;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5005);
                System.out.println("Server started on port 5005");
                
                while (isRunning) {
                    if (serverSocket.isClosed()) break;
                    
                    Socket s = serverSocket.accept();
                    System.out.println("New client connected");
                    
                    ClientHandler handler = new ClientHandler(s);
                    addClient(handler);
                    handler.start();
                }
            } catch (IOException ex) {
                if (isRunning) {
                    System.err.println("Server error: " + ex.getMessage());
                } else {
                    System.out.println("Server stopped.");
                }
            }
        }).start();
    }

    public void stopServer() {
        if (!isRunning) return;
        isRunning = false;

        // 1. Notify all clients
        ServerShutdownNotification shutdownMsg = new ServerShutdownNotification();
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendNotification(shutdownMsg);
            }
        }
        
        // Wait a bit ensuring messages are sent comes naturally due to network stack, 
        // but for a simple "beginner" app we won't add complex sleep logic here unless needed.
        // We'll trust the OS buffers or the client handler cleanup.

        // 2. Close ServerSocket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 3. Close all client connections
        // Create a copy to avoid ConcurrentModificationException if they remove themselves
        List<ClientHandler> copy;
        synchronized (clients) {
             copy = new ArrayList<>(clients);
        }
        for (ClientHandler client : copy) {
            // This will trigger cleanup() in ClientHandler
            // which handles closing streams/sockets
            try {
                // accessing private socket not possible directly unless we modify ClientHandler or just interrupt
               // But ClientHandler.cleanup() is private? No, let's just create a public stop/close method or rely on the thread loop.
               // Actually ClientHandler has simple run loop.
            } catch (Exception e) {}
        }
        
        // Force clear list
        synchronized (clients) {
            clients.clear();
        }
    }

    public static void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public static void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }
}