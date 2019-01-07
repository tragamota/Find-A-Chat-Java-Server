package Message;

import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonConverter;

import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageWrapper {
    private static final Genson jsonConverter = new Genson();

    public static String wrapUnreadMessage(List<Message> messages) {
        Map<String, Object> json = new HashMap<String, Object>() {{
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
        Map<String, Object> json = new HashMap<String, Object>() {{
            put("command", "locations");
            if(info.isEmpty()) {
                put("state", "no users");
            }
            else {
                put("list", info);
            }
        }};
        System.out.println(jsonConverter.serialize(json));
        return jsonConverter.serialize(json);
    }

    public static String wrapLocationRequest() {
        Map<String, Object> json = new HashMap<String, Object>() {{
            put("command", "location request");
        }};
        return jsonConverter.serialize(json);
    }

    public static String wrapLoginInfo(String userHash) {
        Map<String, Object> json = new HashMap<String, Object>() {{
            put("command", "loginInfo");
            if(userHash != null) {
                put("userhash", userHash);
            }
            else {
                put("error", "not valid login");
            }
        }};
        return jsonConverter.serialize(json);
    }

    public static String wrapNewChat(String chatId, String fromId, String toId) {
        Map<String, Object> json = new HashMap<String, Object>() {{
            put("command", "new Chat");
            put("chatID", chatId);
            put("fromID", fromId);
            put("toID", toId);
        }};
        return jsonConverter.serialize(json);
    }

    public static String wrapTempIdToken(String tempHash) {
        Map<String, Object> json = new HashMap<String, Object>() {{
            put("command", "temp idToken");
            put("tempHash", tempHash);
        }};
        return jsonConverter.serialize(json);
    }

    public static String wrapNewMessage(Message message) {
        Map<String, Object> json = new HashMap<String, Object>() {{
            put("command", "newMessage");
            put("message", message);
        }};
        return jsonConverter.serialize(json);
    }

    public static HashMap<String, Object> unwrapMessage(String json) {
        return jsonConverter.deserialize(json, HashMap.class);
    }

    public static Location deserializeLocation(HashMap json) {
        if(json != null) {
            return new Location((double) json.get("latitude"), (double) json.get("longitude"));
        }
        return null;
    }

    public static LoginInfo deserializeLoginInfo(HashMap login) {
        if(login != null) {
            return new LoginInfo((String) login.get("userName"),(String) login.get("passWord"),(String) login.get("idToken"));
        }
        return null;
    }

    public static UserInfo deserializeUserInfo(HashMap user) {
        if(user != null) {
            return new UserInfo((String) user.get("idToken"), (String) user.get("nickName"), (String) user.get("firstName"), (String) user.get("lastName"));
        }
        return null;
    }

    public static Message deserializeChatMessage(HashMap message) {
        if(message != null) {
            return new Message((String) message.get("chatID"), (String) message.get("fromId"), (String) message.get("toId"), (String) message.get("messageText"), LocalDateTime.now());
        }
        return null;
    }
}
