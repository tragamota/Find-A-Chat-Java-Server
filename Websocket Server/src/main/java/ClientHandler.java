import Message.Location;
import Message.MessageWrapper;
import org.java_websocket.WebSocket;

import java.util.*;

public class ClientHandler implements Runnable {
    private WebSocket clientSocket;
    private Queue<Map<String, Object>> messageOrder;
    private String idToken;
    private Timer updateTimer;

    private ClientListener listener;
    private DataBaseConnector dbConnector;

    public ClientHandler(WebSocket clientSocket, String idToken, ClientListener listener) {
        this.clientSocket = clientSocket;
        this.idToken = idToken;
        this.listener = listener;
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
            updateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    sendMessage(MessageWrapper.wrapLocationRequest());
                }
            }, 0, 20000);
        }
    }

    private void processInComingMessage(Map<String, Object> json) {
        String command = (String) json.get("command");
        switch(command) {
            case "gps":
                dbConnector.updateGPS(idToken, json.get("location"));
                break;
        }
        System.out.println(json.keySet());
    }

    private void cleanUp() {
        //remove location from database
        dbConnector.closeConnection();
        updateTimer.cancel();
        updateTimer.purge();
        listener.clientIsDone(this);
    }

    private void validAuth() {
        if(idToken.equals("/")) {
            clientSocket.close();
        }
        else {
            boolean tokenExist = dbConnector.checkIdToken(idToken.replaceFirst("/", ""));
            if(!tokenExist) {
                clientSocket.close();
            }
            else {
                idToken = idToken.replaceFirst("/", "");
                clientSocket.send(MessageWrapper.wrapUnreadMessage(dbConnector.getAllUnreadMessages(idToken)));
            }
        }
    }
}
