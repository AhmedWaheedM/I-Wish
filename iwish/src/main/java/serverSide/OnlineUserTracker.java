package serverSide;

import java.util.HashMap;
import java.util.Map;

public class OnlineUserTracker {
    public static Map<Integer, ClientHandler> onlineUsers ;

    static {
        onlineUsers = new HashMap<Integer, ClientHandler>();
    }
}
