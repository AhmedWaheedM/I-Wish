package serverSide;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dtos.Request;
import models.User;

public class ClientHandler extends Thread {

    private final Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private User currentUser;
    private volatile boolean isRunning = true;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush(); 
            inputStream = new ObjectInputStream(socket.getInputStream());

            while (isRunning) {
                Request request = (Request) inputStream.readObject();
                Object response = RequestRouter.handleRequest(request);

                if(response instanceof User){
                    User user = (User) response;
                    OnlineUserTracker.onlineUsers.put(user.getUserId(), this);
                }
                if(response ==null){
                    response = "ERROR";
                }
                outputStream.writeObject(response);
                outputStream.flush();
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    public void sendNotification(Object msg) {
        try {
            System.err.println("FROMM client handler Sending notification to client: " + msg);
            outputStream.writeObject(msg);
            outputStream.flush();
        } catch (Exception e) {
            cleanup(); 
        }
    }

    private void cleanup() {
        isRunning = false;
        
        // Remove from Server's client list
        Server.removeClient(this);

        try {
            if (inputStream != null) inputStream.close();
        } catch (Exception ignored) { }

        try {
            if (outputStream != null) outputStream.close();
        } catch (Exception ignored) { }

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception ignored) { }
    }
}
