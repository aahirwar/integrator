package ust.tools.bot.integrator.model.xmpp.smack;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.WordUtils;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;

import ust.tools.bot.integrator.model.core.Session;
import ust.tools.bot.integrator.model.util.Cache;


public class MultiUserClient {

    public static final Map<String, Session> USER_SESSIONS = new HashMap<String, Session>();
    public static MultiUserChat MULTI_CHAT;
    public static AbstractXMPPConnection CONNECTION;


    public static void sendMessage2Room(String message, String fromName) throws Exception {
        Message msg = new Message();
        if (USER_SESSIONS.size() > 1) {
            message = "@" + WordUtils.capitalize(fromName.replaceAll("\\.", " ")) + " : " + message;
            XHTMLExtension xhtmlExtension = new XHTMLExtension();
            message = message.replaceAll("\\n", "<br/>");
            Session session = USER_SESSIONS.get(fromName);
            String color = session.getColor();
            if (color == null) {
                color = Cache.getColor();
                session.setColor(color);
            }
            message = "<body><p style='color:" + color + "'>" + message + "</p></body>";
            System.out.println(message);
            xhtmlExtension.addBody(message);
            msg.addExtension(xhtmlExtension);
        }
        msg.setTo(fromName);
        msg.setBody(message);
        MULTI_CHAT.sendMessage(msg);
    }

    public static void sendMessage2Room(String message) throws Exception {
        MULTI_CHAT.sendMessage(message);
    }

    public static void announce(String message) throws Exception {
        Message msg = new Message();
        message = "QUEUE UPDATE : " + message;
        XHTMLExtension xhtmlExtension = new XHTMLExtension();
        message = message.replaceAll("\\n", "<br/>");
        message = "<body><b>" + message + "</b></body>";
        System.out.println(message);
        xhtmlExtension.addBody(message);
        msg.addExtension(xhtmlExtension);
        msg.setBody(message);
        MULTI_CHAT.sendMessage(msg);
    }


    public static void sendMessage(String body, String toJid) {
        try {
            Message message = new Message(toJid, body);
            message.setFrom("MITRA");
            CONNECTION.sendStanza(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String random(int size) {

        StringBuilder generatedToken = new StringBuilder();
        try {
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            // Generate 20 integers 0..20
            for (int i = 0; i < size; i++) {
                generatedToken.append(number.nextInt(9));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedToken.toString();
    }


    public static boolean initializeConnection(String userName, String password, String resource) {
        AbstractXMPPConnection connection = null;
        try {
            try {
                if (CONNECTION != null) {
                    CONNECTION.disconnect();
                }
            } catch (Exception e) {
                System.out.println("Exception While Disconnecting MITRA");
            }
            // Create the configuration for this new connection
            XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            configBuilder.setSocketFactory(sc.getSocketFactory());
            //configBuilder.setDebuggerEnabled(true);
            configBuilder.setSendPresence(true);
            configBuilder.setUsernameAndPassword(userName, password);
            configBuilder.setResource(resource);
            configBuilder.setServiceName("oracle.com");
            configBuilder.setHost("stbeehive.oracle.com");
            TLSUtils.acceptAllCertificates(configBuilder);
            configBuilder.setPort(5223);
            connection = new XMPPTCPConnection(configBuilder.build());
            // Connect to the server
            connection.connect();
            //System.out.println("Connected");
            // Log into the server
            connection.login();
            //System.out.println("Logged in");
            CONNECTION = connection;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean joinChat(String name) {
        boolean isJoined = true;
        try {
            MultiUserChatManager mucm = MultiUserChatManager.getInstanceFor(CONNECTION);
            String nameWithDomain = name + "@conference.oracle.com";
            MultiUserChat muc = mucm.getMultiUserChat(nameWithDomain);
            PacketReceiver roomListener = new PacketReceiver();
            MULTI_CHAT = muc;
            MULTI_CHAT.addMessageListener(roomListener);
            MULTI_CHAT.join(name);
        } catch (Exception e) {
            e.printStackTrace();
            isJoined = false;
        }
        return isJoined;
    }

    public static Message nextMessage(long time) {
        Message message = null;
        try {
            message = MULTI_CHAT.nextMessage(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;

    }

    public static String getFromName(Message msg) {
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

    public static String getSenderEmailId(Message msg) {
        String emailId = null;
        try {
            String fromName = getFromName(msg);
            System.out.println("fromName from msg : " + fromName);
            MULTI_CHAT.getOccupants();
            List<Occupant> occupants = MULTI_CHAT.getParticipants();
            for (Occupant occupant : occupants) {
                if (fromName.equalsIgnoreCase(occupant.getNick())) {
                    emailId = occupant.getJid().split("\\/")[0];
                    System.out.println("Retrieve Saved Email :" + fromName + " : " + emailId);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailId;
    }

    public static Session createSession(String fromName, String userName, Message message) {
        Session session = new Session();
        session.setUserName(fromName);
        System.out.println("UserName : " + userName);
        String userEmailId = getSenderEmailId(message);
        System.out.println("fromName : " + fromName + " -> userEmailId : " + userEmailId);
        if (userEmailId == null) {
            userEmailId = fromName + "@oracle.com";
        }
        session.setUserEmailAddress(userEmailId);
        return session;
    }
}
