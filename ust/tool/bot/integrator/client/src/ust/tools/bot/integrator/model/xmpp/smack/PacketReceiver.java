package ust.tools.bot.integrator.model.xmpp.smack;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;

import ust.tools.bot.integrator.model.core.Session;


public class PacketReceiver implements MessageListener {

    private Queue<Message> messages = new LinkedList<Message>();


    public void processMessage(Message msg) {
        messages.add(msg);
    }

    public Message getMessage() {
        Message message = messages.peek();
        if (message != null) {
            String fromName = this.getFromName(message);
            System.out.println(fromName + ":" + message.getBody());
            messages.remove();
        }
        return message;
    }

    public String getFromName(Message msg) {
        String fromName = "";
        String[] from = msg.getFrom().split("\\/");
        if (from.length == 2) {
            fromName = from[1];
        } else {
            fromName = from[0];
        }
        System.out.println(fromName + " : " + msg.getBody());
        return fromName;
    }

    public String getSenderEmailId(Message msg, MultiUserChat multiChat) {
        String emailId = null;
        try {
            String fromName = this.getFromName(msg);
            List<Occupant> occupants = multiChat.getParticipants();
            for (Occupant occupant : occupants) {
                if (fromName.equalsIgnoreCase(occupant.getNick())) {
                    emailId = occupant.getJid().split("\\/")[0];
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailId;
    }

    public Session createSession(String fromName, String userName, Message message, MultiUserChat muc) {
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
