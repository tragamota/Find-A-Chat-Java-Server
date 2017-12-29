package Message;

import com.owlike.genson.Genson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageWrapper {
    private static final Genson jsonConverter = new Genson();

    public static String wrapUnreadMessage(List<Message> messages) {
        Map<String, Object> json = new HashMap<String, Object>() {{
            put("Command", "UnreadMessages");
            if(messages.isEmpty()) {
                put("State", "No Messages");
            }
            else {
                put("List", messages);
            }
        }};
        return jsonConverter.serialize(json);
    }

    public static String wrapLocations(List<UserInfo> info) {
        Map<String, Object> json = new HashMap<String, Object>() {{
            put("Command", "Locations");
            if(info.isEmpty()) {
                put("State", "NoUsers");
            }
            else {
                put("List", info);
            }
        }};
        return jsonConverter.serialize(json);
    }

}
