package Client;

import Connection.DataBaseConnector;
import Message.LoginInfo;
import Message.Message;
import Message.MessageWrapper;
import Message.UserInfo;
import org.java_websocket.WebSocket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class ClientHandler implements Runnable {
    private WebSocket clientSocket;
    private Queue<Map<String, Object>> messageOrder;

    private String idToken;
    private ClientLoginState loginStatus;
    private Timer updateTimer;

    private ClientListener listener;
    private DataBaseConnector dbConnector;

    public ClientHandler(WebSocket clientSocket, String idToken, ClientListener listener) {
        this.clientSocket = clientSocket;
        this.idToken = idToken;
        this.listener = listener;
        loginStatus = ClientLoginState.UNINDENTIFIED;
        messageOrder = new LinkedList<>();
        updateTimer = new Timer();
    }

    @Override
    public void run() {
        initialize();
        while(clientSocket.isOpen() || clientSocket.isConnecting() || !messageOrder.isEmpty()) {
            if(!messageOrder.isEmpty()) {
                processInComingMessage(messageOrder.poll());
            }

            try {
                Thread.yield();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cleanUp();
    }

    public void sendMessage(String serializedMessage) {
        if(clientSocket.isOpen()) {
            clientSocket.send(serializedMessage);
        }
    }

    public String getIdToken() {
        return idToken;
    }

    public void addMessageToStack(Map<String, Object> json) {
        messageOrder.add(json);
    }

    private void initialize() {
        dbConnector = new DataBaseConnector();
        validAuth();
        if(clientSocket.isOpen() || clientSocket.isConnecting()) {
            if(loginStatus == ClientLoginState.INDENTIFIED) {
                startTimerTask();
            }
            else {
                idToken = LoginInfo.generateIdToken(clientSocket.getRemoteSocketAddress().getHostString() + LocalDateTime.now().toString());
                clientSocket.send(MessageWrapper.wrapTempIdToken(idToken));
            }
        }
    }

    private void processInComingMessage(Map<String, Object> json) {
        String command = (String) json.get("command");
        switch(loginStatus) {
            case INDENTIFIED:
                switch (command) {
                    case "gps":
                        dbConnector.updateGPS(idToken, MessageWrapper.deserializeLocation((HashMap) json.get("location")));
                        sendMessage(MessageWrapper.wrapLocations(dbConnector.getAllLocations()));
                        break;
                    case "message":
                        Message receivedMessage = MessageWrapper.deserializeChatMessage((HashMap) json.get("message"));
                        sendMessage(MessageWrapper.wrapNewMessage(receivedMessage));
                        dbConnector.addMessageToChat(receivedMessage);
                        if(listener.sendMessageTo(MessageWrapper.wrapNewMessage(receivedMessage), receivedMessage.getTo())) {
                            dbConnector.setMessageAsRead(receivedMessage);
                        }
                        break;
                    case "chat":
                        String chatID = dbConnector.startChat(idToken,(HashMap) json);
                        sendMessage(MessageWrapper.wrapNewChat(chatID, idToken, (String) json.get("toId")));
                        listener.sendMessageTo(MessageWrapper.wrapNewChat(chatID, (String) json.get("toId"), idToken),(String) json.get("toId"));
                        break;
                    case "profileImage":
                        saveProfilePicture((String) json.get("image"));
                        dbConnector.saveImagePath(idToken);
                        break;
                }
                break;
            case LOGIN:
                switch (command) {
                    case "login":
                        sendMessage(MessageWrapper.wrapLoginInfo(dbConnector.findIdToken(MessageWrapper.deserializeLoginInfo((HashMap) json))));
                        clientSocket.close();
                        break;
                }
                break;
            case REGISTER:
                switch (command) {
                    case "new user":
                        String userHash = dbConnector.newUser(MessageWrapper.deserializeLoginInfo((HashMap) json.get("login")), MessageWrapper.deserializeUserInfo((HashMap) json.get("user")));
                        sendMessage(MessageWrapper.wrapLoginInfo(userHash));
                        clientSocket.close();
                        break;
                }
                break;
        }
    }

    private void saveProfilePicture(String base64Image) {
        BufferedImage imageToSave = UserInfo.decodeImage(base64Image);

        try {
            File file = new File(String.valueOf(Paths.get(Paths.get("").toAbsolutePath() + "\\profileImage\\" + idToken + ".png")));
            ImageIO.write(imageToSave, "png", file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTimerTask() {
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendMessage(MessageWrapper.wrapLocationRequest());
            }
        }, 0, 30000);
    }

    private void cleanUp() {
        //remove location from database
        updateTimer.cancel();
        dbConnector.deleteLocation(idToken);
        dbConnector.closeConnection();
        listener.clientIsDone(this);
    }

    private void validAuth() {
        if(idToken.equals("/")) {
            clientSocket.close();
        }
        else if(idToken.equals("/login")) {
            loginStatus = ClientLoginState.LOGIN;
        }
        else if(idToken.equals("/register")) {
            loginStatus = ClientLoginState.REGISTER;
        }
        else {
            idToken = idToken.replaceFirst("/", "");
            if(!dbConnector.checkIdToken(idToken)) {
                clientSocket.close();
            }
            else {
                clientSocket.send(MessageWrapper.wrapUnreadMessage(dbConnector.getAllUnreadMessages(idToken)));
                loginStatus = ClientLoginState.INDENTIFIED;
            }
        }
    }
}
