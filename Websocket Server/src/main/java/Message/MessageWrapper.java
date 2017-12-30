package Message;

import com.owlike.genson.Genson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageWrapper {
    private static final Genson jsonConverter = new Genson();

    public static String wrapUnreadMessage(List<Message> messages) {
        Map<String, Object> json = new HashMap<>() {{
            put("command", "unread messages");
            if(messages.isEmpty()) {
                put("state", "no Messages");
            }
            else {
                put("list", messages);
            }
        }};
        return jsonConverter.serialize(json);
    }

    public static String wrapLocations(List<UserInfo> info) {
        Map<String, Object> json = new HashMap<>() {{
            put("command", "locations");
            if(info.isEmpty()) {
                put("state", "no users");
            }
            else {
                put("list", info);
            }
        }};
        return jsonConverter.serialize(json);
    }

    public static String wrapLocationRequest() {
        Map<String, Object> json = new HashMap<>() {{
            put("command", "location request");
        }};
        return jsonConverter.serialize(json);
    }

    public static HashMap<String, Object> unwrapMessage(String json) {
        return jsonConverter.deserialize(json, HashMap.class);
    }
}
