package ust.tools.bot.integrator.model.xmpp.smack;

import java.util.LinkedList;
import java.util.Queue;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import ust.tools.bot.integrator.model.core.Session;

public class MyChatMessageListener implements ChatMessageListener {


    private Queue<ChatInfo> chatInfos = new LinkedList<ChatInfo>();

    @Override
    public void processMessage(Chat chat, Message message) {
        chatInfos.add(new ChatInfo(message, chat));
    }

    public ChatInfo getChatInfo() {
        Message message = null;
        ChatInfo cf = chatInfos.peek();
        if (cf != null) {
            message = cf.getMessage();
            if (message != null) {
                System.out.println(message.getType());
                String fromName = this.getFromName(message);
                System.out.println(fromName + ":" + message.getBody());
                chatInfos.remove();
            }
        }
        return cf;
    }

    public String getFromName(Message msg) {
        String fromName = "";
        System.out.println("From Name : " + msg.getFrom());
        System.out.println("To Name : " + msg.getTo());
        String[] from = msg.getFrom().split("\\/");
        if (from.length == 2) {
            fromName = from[1];
        } else {
            fromName = from[0];
        }
        return fromName;
    }

    public String getSenderEmailId(Message msg, Chat multiChat) {
        String emailId = null;
        try {
            
            String fromName = this.getFromName(msg);
            //Occupant occupant = multiChat.getParticipant();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailId;
    }

    public Session createSession(String fromName, String userName, Message message, Chat muc) {
        Session session = new Session();
        session.setUserName(fromName);
        String userEmailId = this.getSenderEmailId(message, muc);
        if (userEmailId == null) {
            userEmailId = userName + "@oracle.com";
        }
        session.setUserEmailAddress(userEmailId);
        return session;
    }

}
