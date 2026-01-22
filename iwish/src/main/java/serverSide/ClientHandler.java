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
    public synchronized void pushMessage(Object msg) {
    try {
        outputStream.writeObject(msg);
        outputStream.flush();
    } catch (Exception e) {
        e.printStackTrace();
    }
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

                if (request instanceof dtos.requestDtos.userHandler.LoginRequest && response instanceof models.User) {
                    this.currentUser = (models.User) response; 
                    SessionManager.registerUser(this.currentUser.getUserId(), this); 
                }
                
                pushMessage(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        isRunning = false;

        try {
            if (inputStream != null) inputStream.close();
        } catch (Exception ignored) { }

        try {
            if (outputStream != null) outputStream.close();
        } catch (Exception ignored) { }

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception ignored) { }
        try {
            if (currentUser != null) SessionManager.unregisterUser(currentUser.getUserId());
        } catch (Exception ignored) { }
    }
}
