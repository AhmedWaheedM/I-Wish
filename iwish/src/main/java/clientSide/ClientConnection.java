package clientSide;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private boolean running;

    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        running = true;
    }

    public synchronized Object sendAndWait(Object request) throws Exception {
        if (out == null || in == null)
            throw new IllegalStateException("Not connected. Call connect() first.");

        out.writeObject(request);
        out.flush();

        return in.readObject();
    }


    public void close() {
        running = false;

        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (Exception ignored) {}
    }
}
