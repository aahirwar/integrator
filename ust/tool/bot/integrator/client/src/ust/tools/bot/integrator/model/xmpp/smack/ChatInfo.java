package ust.tools.bot.integrator.model.xmpp.smack;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

public class ChatInfo {
    public ChatInfo() {
        super();
    }
    private Message message;
    private Chat chat;
    public ChatInfo(Message message, Chat chat) {
        this.message = message;
        this.chat = chat;
    }
    

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Chat getChat() {
        return chat;
    }
}
