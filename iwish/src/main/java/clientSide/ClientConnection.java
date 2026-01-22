package clientSide;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnection {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final BlockingQueue<Object> responseQueue = new LinkedBlockingQueue<>();

    private boolean running;

    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        running = true;
        startListening();
    }

    public synchronized Object sendAndWait(Object request) throws Exception {
        out.writeObject(request);
        out.flush();
        return responseQueue.take();
    }


    public void close() {
        running = false;

        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (Exception ignored) {}
    }

    public void startListening() {
        new Thread(() -> {
            try {
                while (running) {
                    Object response = in.readObject();
                    if (response instanceof dtos.NotificationDTO) {
                        System.out.println("Notification: " + ((dtos.NotificationDTO)response).getMessage());
                    } else {
                        responseQueue.put(response);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
