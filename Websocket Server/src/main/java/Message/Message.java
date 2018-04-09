package Message;

import java.time.LocalDateTime;

public class Message {
    private String chatId, from, to, content;
    private LocalDateTime time;

    public Message(String chatId, String from, String to, String content, LocalDateTime time) {
        this.chatId = chatId;
        this.from = from;
        this.content = content;
        this.to = to;
        this.time = time;
    }

    public String getChatId() {
        return chatId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() { return to; }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
