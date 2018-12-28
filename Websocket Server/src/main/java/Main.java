import Connection.ServerConnection;

public class Main {
    private ServerConnection socket;

    public Main() {
        socket = new ServerConnection(443);
        socket.setConnectionLostTimeout(2000);
        socket.start();
    }

    public static void main(String[] args) {
        new Main();
    }
}
