package ServerSide;

public class UserHandler extends Thread{
    DataInputStream dis;
    PrintStream ps;
    static Vector<UserHandler> clientsVector = new Vector<UserHandler>();

    public UserHandler(Socket cs) {
        try {
            dis = new DataInputStream(cs.getInputStream());
            ps = new PrintStream(cs.getOutputStream());

            UserHandler.clientsVector.add(this);

            start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            while (true) {
                String str = br.readLine();
                if (str != null) {
                    sendMessageToAll(str);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
