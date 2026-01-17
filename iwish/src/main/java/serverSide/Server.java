package serverSide;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(5005);
            
            while (true) {
                Socket s = serverSocket.accept();
                new ClientHandler(s);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}