import Connection.DataBaseConnector;
import Connection.ServerConnection;
import Message.LoginInfo;
import Message.Message;
import Message.UserInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Main {
    private ServerConnection socket;

    public Main() {
        socket = new ServerConnection(443);
        socket.setConnectionLostTimeout(20);
        socket.start();
        DataBaseConnector db = new DataBaseConnector();
    }

    public static void main(String[] args) {
        new Main();
    }
}
