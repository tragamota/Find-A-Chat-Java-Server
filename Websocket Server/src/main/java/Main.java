import Message.UserInfo;
import com.owlike.genson.Genson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private ServerConnection socket;

    public Main() {
        socket = new ServerConnection(443);
        socket.setConnectionLostTimeout(20);
        socket.start();
    }

    public static void main(String[] args) {
        new Main();
    }
}
