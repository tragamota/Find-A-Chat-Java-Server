

public interface ClientListener {
    void clientIsDone(ClientHandler clientHandler);
    boolean sendMessageTo(String jsonMessage);
}