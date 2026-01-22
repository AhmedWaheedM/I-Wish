package clientSide;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import clientSide.views.NotificationPanel;
import dtos.Notification;
import javafx.application.Platform;

public class ClientConnection {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private volatile boolean running;
    private Thread listenerThread;

    private NotificationPanel notificationPanel;

    private final Object lock = new Object();
    private Object lastResponse = null;

    public void setNotificationPanel(NotificationPanel panel) {
        this.notificationPanel = panel;
    }

    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        running = true;
        startListening();
    }


    public Object sendAndWait(Object request) throws Exception {
        if (out == null || in == null)
            throw new IllegalStateException("Not connected. Call connect() first.");

        synchronized (lock) {
            lastResponse = null;

            out.writeObject(request);
            out.flush();

            while (running && lastResponse == null) {
                lock.wait();
            }

            if (!running) {
                throw new IllegalStateException("Disconnected from server.");
            }

            return lastResponse;
        }
    }

    private void startListening() {
        if (in == null) throw new IllegalStateException("Not connected. Call connect() first.");
        if (listenerThread != null && listenerThread.isAlive()) return;

        listenerThread = new Thread(() -> {
            try {
                while (running) {
                    Object msg = in.readObject();

                    if (msg instanceof Notification) {
                        Notification n = (Notification) msg;

                        Platform.runLater(() -> {
                            if (notificationPanel != null) {
                                notificationPanel.show(n);
                            }
                        });
                        continue;
                    }

                    synchronized (lock) {
                        lastResponse = msg;
                        lock.notifyAll();
                    }
                }
            } catch (EOFException | SocketException e) {
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                synchronized (lock) {
                    running = false;
                    lock.notifyAll();
                }
                close();
            }
        }, "ClientConnection-Listener");

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void close() {
        running = false;

        synchronized (lock) {
            lock.notifyAll();
        }

        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (Exception ignored) {}
    }
}
