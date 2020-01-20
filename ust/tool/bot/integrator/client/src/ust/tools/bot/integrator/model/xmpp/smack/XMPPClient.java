package ust.tools.bot.integrator.model.xmpp.smack;

import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;


public class XMPPClient {
    public static void main(String[] args) {

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
            configBuilder.setUsernameAndPassword("umasankar.tulasi", "umainT9O$0");
            configBuilder.setResource("FAB");
            configBuilder.setServiceName("oracle.com");
            configBuilder.setHost("stbeehive.oracle.com");
            TLSUtils.acceptAllCertificates(configBuilder);
            configBuilder.setPort(5223);
            AbstractXMPPConnection connection = new XMPPTCPConnection(configBuilder.build());
            // Connect to the server
            connection.connect();
            // Log into the server
            System.out.println("Connected");
            connection.login();
            System.out.println("Logged in");


            MyChatMessageListener cml = new MyChatMessageListener();
            ChatManager cm = ChatManager.getInstanceFor(connection);
            cm.addChatListener(new ChatManagerListener() {
                public void chatCreated(final Chat chat, final boolean createdLocally) {
                    chat.addMessageListener(cml);
                }
            });

            while (true) {
                try {
                    ChatInfo chatInfo = cml.getChatInfo();
                    if (chatInfo != null) {
                        System.out.println(chatInfo.getChat().getParticipant() + " : " +
                                           chatInfo.getMessage().getBody());
                    }
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


