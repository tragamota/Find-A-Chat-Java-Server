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
        UserInfo person = new UserInfo("12345", "Tragamota", "Ian", "Van de Poll", "profielfoto.jpg",  20.7777777, 53.5342342);
        List<UserInfo> persons = new ArrayList<>();
        persons.add(person);
        persons.add(person);
        Map<String, Object> json = new HashMap<String, Object>() {{
            put("Command", "locations");
            put("List", persons);
        }};
        System.out.println(new Genson().serialize(json));
    }
}
