package Message;

import java.time.LocalDateTime;

public class Message {
    private String chatId, to, from, content;
    private LocalDateTime time;

    public Message(String chatId, String to, String from, String content, LocalDateTime time) {
        this.chatId = chatId;
        this.to = to;
        this.from = from;
        this.content = content;
        this.time = time;
    }

    public String getChatId() {
        return chatId;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
