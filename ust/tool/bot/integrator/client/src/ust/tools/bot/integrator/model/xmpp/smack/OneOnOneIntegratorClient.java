package ust.tools.bot.integrator.model.xmpp.smack;

import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;

import ust.tools.bot.integrator.model.lang.Messages;
import ust.tools.bot.integrator.model.core.AbstractProcessor;
import ust.tools.bot.integrator.model.core.Session;
import ust.tools.bot.integrator.model.core.Step;
import ust.tools.bot.integrator.model.processors.DeploymentRequestProcessor;
import ust.tools.bot.integrator.model.processors.DeploymentSummaryProcessor;
import ust.tools.bot.integrator.model.processors.ManageDeploymentsProcessor;
import ust.tools.bot.integrator.model.util.Cache;


public class OneOnOneIntegratorClient {

    private static Map<String, Session> USER_SESSIONS = new HashMap<String, Session>();

    private static String pCode = "DOS";

    private static Long SESSION_EXPIRY_TIME_IN_MILLIS = Long.valueOf(1000 * 60 * 5);

    private static Long SESSION_WARNING_TIME_IN_MILLIS = SESSION_EXPIRY_TIME_IN_MILLIS - Long.valueOf(1000 * 60 * 1);

    public static void main(String[] args) {
        String userName = "umasankar.tulasi";
        String productCode = args[0];
        if (productCode == null) {
            productCode = pCode;
        } else {
            pCode = productCode;
        }
        try {
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
            configBuilder.setUsernameAndPassword(userName, "umainT9O$0");
            configBuilder.setResource("DOS");
            configBuilder.setServiceName("oracle.com");
            configBuilder.setHost("stbeehive.oracle.com");
            TLSUtils.acceptAllCertificates(configBuilder);
            configBuilder.setPort(5223);
            AbstractXMPPConnection connection = new XMPPTCPConnection(configBuilder.build());
            
            
            // Connect to the server
            connection.connect();
            System.out.println("Connected");
            // Log into the server
            connection.login();
            System.out.println("Logged in");

            Cache.refresh();
            System.out.println("Loaded Cache");


            try {
                MyChatMessageListener muc = new MyChatMessageListener();
                ChatManager cm = ChatManager.getInstanceFor(connection);
                cm.addChatListener(new ChatManagerListener() {
                    public void chatCreated(final Chat chat, final boolean createdLocally) {
                        chat.addMessageListener(muc);
                    }
                });
                while (true) {
                    try {
                        ChatInfo chatInfo = muc.getChatInfo();
                        if (chatInfo != null) {
                            Message message = chatInfo.getMessage();
                            if (message != null && message.getBody() != null) {
                                String fromName = muc.getFromName(message);
                                Session session = USER_SESSIONS.get(fromName);
                                if (session == null || session.isExpired()) {
                                    session = muc.createSession(fromName, userName, message, chatInfo.getChat());
                                    session.setProductCode(productCode);
                                    USER_SESSIONS.put(fromName, session);
                                    String userEmail = session.getUserEmailAddress();
                                    if (Cache.getIntgEmails().contains(userEmail.toLowerCase())) {
                                        session.setIntegrator(true);
                                    }
                                    session.setLastAccessedTimeInMillis(System.currentTimeMillis());
                                } else {
                                    session.setLastAccessedTimeInMillis(System.currentTimeMillis());
                                }
                                Step nextStep = processMessage(fromName, message);
                                sendMessage(nextStep.getPromptMessage(), fromName, chatInfo.getChat());
                                while (nextStep.isInformational()) {
                                    nextStep = processMessage(fromName, null);
                                    sendMessage(nextStep.getPromptMessage(), fromName, chatInfo.getChat());
                                }
                            }
                        } else {
                            //validateSessions(chatInfo.getChat());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Step processMessage(String fromName, Message message) {
        String text = null;
        if (message != null) {
            text = message.getBody();
            text = text.replaceAll("  ", " ");
        }
        Session session = USER_SESSIONS.get(fromName);
        AbstractProcessor processor = session.getProcessor();
        if (processor == null) {
            String menu = Messages.MENU;
            if ("1".equalsIgnoreCase(text)) {
                processor = new DeploymentRequestProcessor();
            } else if ("2".equalsIgnoreCase(text)) {
                processor = new DeploymentSummaryProcessor();
            } else if ("3".equalsIgnoreCase(text)) {
                if (session.isIntegrator()) {
                    processor = new ManageDeploymentsProcessor();
                }
            } else if (session.isIntegrator()) {
                menu = Messages.INTG_MENU;
            }
            if (processor == null) {
                return new Step(-1, Messages.SN_MENU, null, menu, false);
            } else {
                session.setProcessor(processor);
                processor.setUserSession(session);
            }
        }
        Step step = processor.getCurrentStep();
        if (step != null && text != null) {
            step.setUserInput(text);
        }
        processor = session.getProcessor();
        Step nextStep = processor.processStep(step);
        processor.setCurrentStep(nextStep);
        if (nextStep.isLastStep()) {
            session.setProcessor(null);
        }
        return nextStep;
    }


    private static void validateSessions(Chat muc) throws Exception {
        Long currentTimeInMillis = System.currentTimeMillis();
        Set<String> users = USER_SESSIONS.keySet();
        List<String> expiredUserNames = new ArrayList<String>();
        for (String userName : users) {
            Session session = USER_SESSIONS.get(userName);
            if (!session.isExpired() && !session.hasNoExpiry()) {
                Long lastAccesedTimeImeInMillis = session.getLastAccessedTimeInMillis();
                Long timeSinceAccessedInMillis = currentTimeInMillis - lastAccesedTimeImeInMillis;
                if (timeSinceAccessedInMillis > SESSION_EXPIRY_TIME_IN_MILLIS) {
                    session.setExpired(true);
                    expiredUserNames.add(userName);
                    sendMessage(Messages.SESSION_EXPIRED, userName, muc);
                    System.out.println(userName + "'s session expired");
                } else if (!session.isWarned() && timeSinceAccessedInMillis > SESSION_WARNING_TIME_IN_MILLIS) {
                    session.setWarned(true);
                    sendMessage(Messages.EXPIRY_WARNING, userName, muc);
                    System.out.println(userName + "'s session will expire in a minute");
                }
            }
        }
        for (String userName : expiredUserNames) {
            USER_SESSIONS.remove(userName);
        }
    }


    private static void sendMessage(String message, String fromName, Chat muc) throws Exception {
        Message msg = new Message();
        msg.setTo(fromName);
        msg.setBody(message);
        muc.sendMessage(msg);
    }
    
    
//    public void listeningForMessages(AbstractXMPPConnection xmppConnection) {
//           PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class));
//           PacketCollector collector = xmppConnection.createPacketCollector(filter);
//           while (true) {
//               Packet packet = collector.nextResult();
//               if (packet instanceof Message) {
//                   Message message = (Message) packet;
//                   if (message != null && message.getBody() != null)
//                       System.out.println("Received message from "
//                               + packet.getFrom() + " : "
//                               + (message != null ? message.getBody() : "NULL"));
//               }
//           }
//       }
//    


}
