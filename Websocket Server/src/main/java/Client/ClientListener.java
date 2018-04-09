package Client;

public interface ClientListener {
    void clientIsDone(ClientHandler clientHandler);
    boolean sendMessageTo(String jsonMessage, String toId);
}