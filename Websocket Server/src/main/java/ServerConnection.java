import Message.MessageWrapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServerConnection extends WebSocketServer {
    private List<ClientHandler> clients;
    private ClientListener listener;

    public ServerConnection(int portNumber) {
        super(new InetSocketAddress(portNumber));
        setWebSocketFactory(new DefaultSSLWebSocketServerFactory(getSSLContext()));

        clients = new ArrayList<>();
        listener = new ClientListener() {
            @Override
            public void clientIsDone(ClientHandler clientHandler) {
                Iterator it = clients.iterator();
                while(it.hasNext()) {
                    if(it.next() == clientHandler) {
                        it.remove();
                    }
                }
            }

            @Override
            public boolean sendMessageTo(String json) {
                return false;
            }
        };
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        //Create new Thread that handles all the incoming message
        System.out.println(webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
        ClientHandler clientHandler = new ClientHandler(webSocket, clientHandshake.getResourceDescriptor(), listener);
        clients.add(clientHandler);
        new Thread(clientHandler).start();
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        Map<String, Object> json = MessageWrapper.unwrapMessage(s);
        String idToken = (String) json.get("idToken");
        json.remove("idToken");

        Iterator it = clients.iterator();
        while(it.hasNext()) {
            ClientHandler client = (ClientHandler) it.next();
            if(client.getIdToken().equals(idToken)) {
                client.addMessageToStack(json);
                return;
            }
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started");
    }

    private SSLContext getSSLContext() {
        SSLContext SSLCertificate = null;

        String storeType = "JKS";
        String keyStore = "ourCertificate.jks";
        String storePass = "AvansPassword";
        String keyPass = "AvansPassword";

        try {
            KeyStore ks = KeyStore.getInstance(storeType);
            File file = new File(getClass().getResource(keyStore).toURI());
            ks.load(new FileInputStream(file), storePass.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunX509");
            kmf.init(ks, keyPass.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunX509");
            tmf.init(ks);

            SSLCertificate = SSLContext.getInstance("TLS");
            SSLCertificate.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return SSLCertificate;
    }
}
