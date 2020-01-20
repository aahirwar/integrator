import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jivesoftware.smack.packet.Message;

import ust.tools.bot.integrator.model.core.AbstractProcessor;
import ust.tools.bot.integrator.model.core.Session;
import ust.tools.bot.integrator.model.core.Step;
import ust.tools.bot.integrator.model.lang.Messages;
import ust.tools.bot.integrator.model.processors.DeploymentRequestProcessor;
import ust.tools.bot.integrator.model.processors.DeploymentSummaryProcessor;
import ust.tools.bot.integrator.model.processors.ManageDeploymentsProcessor;
import ust.tools.bot.integrator.model.util.Cache;
import ust.tools.bot.integrator.model.util.Util;
import ust.tools.bot.integrator.model.xmpp.smack.MultiUserClient;

import java.util.Map;

public class Mitra {

    private static final String USER_NAME_MITRA = "mitra_ww";
    private static final String USER_WORD_MITRA = "Mitra123";

    public static final String USER_NAME ="anil.ahirwar";
    public static final String USER_WORD = "Wrong$Pwd";

    
    public static  String pCode = "DOS";

    private static Long SESSION_EXPIRY_TIME_IN_MILLIS = Long.valueOf(1000 * 60 * 5);

    private static Long SESSION_WARNING_TIME_IN_MILLIS = SESSION_EXPIRY_TIME_IN_MILLIS - Long.valueOf(1000 * 60 * 1);

    public static void main(String[] args) {
        Logger rootLogger = Logger.getLogger("");
        Handler logHandler = new ConsoleHandler();
        logHandler.setFormatter(new SimpleFormatter());
        logHandler.setLevel(Level.SEVERE);
        rootLogger.removeHandler(rootLogger.getHandlers()[0]);
        rootLogger.addHandler(logHandler);

        boolean isInitialized = Util.initializeRAM();
        if (!isInitialized) {
            System.out.println("Unable to Intialize MITRA, Exiting...");
            return;
        }

        String code = null;
        if (args != null && args.length > 0) {
            code = args[0];
        }
        if (code != null && "UST".equalsIgnoreCase(code)) {
            code = pCode;
        } else {
            boolean proceed = authenticate();
            if (!proceed) {
                return;
            }
        }

        try {
            System.out.println("Preparing MITRA for " + pCode + "...");
            MultiUserClient.initializeConnection(USER_NAME_MITRA, USER_WORD_MITRA, pCode);            
            Cache.setProductCode(pCode);
            Cache.refresh();
            String name = pCode + "-MITRA";
            MultiUserClient.joinChat(name);
            String nameWithDomain = name + "@conference.oracle.com";
            System.out.println("Loaded Cache for " + pCode);

            try {
                System.out.println("Started Listening to  chat room " + nameWithDomain);
                MultiUserClient.sendMessage2Room(Messages.MENU);
                
                while (true) {
                    System.out.println(new Date());
                    try {
                        // Fix for disconnection of long standing connections - not working though.
                        if (MultiUserClient.CONNECTION == null || !MultiUserClient.CONNECTION.isConnected()) {
                            System.out.println("Reconneting....");
                            MultiUserClient.initializeConnection(USER_NAME, USER_WORD, pCode);
                        }
                        if (MultiUserClient.MULTI_CHAT == null || !MultiUserClient.MULTI_CHAT.isJoined())  {
                            System.out.println("Rejoining....");
                            MultiUserClient.joinChat(name);
                        }

                        Message message = MultiUserClient.nextMessage(600000);
                        if (message != null) {
                            String fromName = MultiUserClient.getFromName(message);
                            if (!nameWithDomain.equalsIgnoreCase(fromName) && !name.equalsIgnoreCase(fromName)) {
                                Session session = MultiUserClient.USER_SESSIONS.get(fromName);
                                System.out.println("Sessions : " + MultiUserClient.USER_SESSIONS);
                                if (session == null || session.isExpired()) {
                                    session = MultiUserClient.createSession(fromName, USER_NAME, message);
                                    System.out.println("Session Created for user " + fromName + " with email : "+ session.getUserEmailAddress());
                                    if (MultiUserClient.USER_SESSIONS.isEmpty()) {
                                        session.setColor("black");
                                    }
                                    session.setProductCode(pCode);
                                    MultiUserClient.USER_SESSIONS.put(fromName, session);
                                    String userEmail = session.getUserEmailAddress();
                                    if (Cache.getIntgEmails().contains(userEmail.toLowerCase())) {
                                        session.setIntegrator(true);
                                    }
                                    session.setLastAccessedTimeInMillis(System.currentTimeMillis());
                                } else {
                                    System.out.println("Session Found for user " + fromName + " with email : "+ session.getUserEmailAddress());
                                    session.setLastAccessedTimeInMillis(System.currentTimeMillis());
                                }
                                Step nextStep = processMessage(fromName, message);
                                MultiUserClient.sendMessage2Room(nextStep.getPromptMessage(), fromName);
                                session.setLastAccessedTimeInMillis(System.currentTimeMillis());
                                while (nextStep.isInformational()) {
                                    nextStep = processMessage(fromName, null);
                                    MultiUserClient.sendMessage2Room(nextStep.getPromptMessage(), fromName);
                                }
                            }
                        } else {
                            validateSessions();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            text = text.trim();
        }
        Session session = MultiUserClient.USER_SESSIONS.get(fromName);
        System.out.println(fromName + " : " + session.getUserEmailAddress());
        
        
        
        for(Map.Entry<String,Session> entry:MultiUserClient.USER_SESSIONS.entrySet()){
            
            System.out.println("SEssions : " + entry.getKey() + " : " + entry.getValue().getUserEmailAddress());
            }
        
        
        
        
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

       // processor = session.getProcessor();
        Step nextStep = processor.processStep(step);

        processor.setCurrentStep(nextStep);
        if (nextStep.isLastStep()) {
            session.setProcessor(null);
        }
        return nextStep;
    }


    private static void validateSessions() throws Exception {
        Long currentTimeInMillis = System.currentTimeMillis();
        Set<String> users = MultiUserClient.USER_SESSIONS.keySet();
        List<String> expiredUserNames = new ArrayList<String>();
        for (String userName : users) {
            Session session = MultiUserClient.USER_SESSIONS.get(userName);
            if (!session.isExpired() && !session.hasNoExpiry()) {
                Long lastAccesedTimeImeInMillis = session.getLastAccessedTimeInMillis();
                Long timeSinceAccessedInMillis = currentTimeInMillis - lastAccesedTimeImeInMillis;
                if (timeSinceAccessedInMillis > SESSION_EXPIRY_TIME_IN_MILLIS) {
                    session.setExpired(true);
                    expiredUserNames.add(userName);
                    System.out.println(userName + "s session expired ");
                    if (session.getProcessor() != null) {
                        MultiUserClient.sendMessage2Room(Messages.SESSION_EXPIRED, userName);
                    }
                } else if (!session.isWarned() && timeSinceAccessedInMillis > SESSION_WARNING_TIME_IN_MILLIS &&
                           session.getProcessor() != null) {
                    session.setWarned(true);
                    MultiUserClient.sendMessage2Room(Messages.EXPIRY_WARNING, userName);
                }
            }
        }
        for (String userName : expiredUserNames) {
            MultiUserClient.USER_SESSIONS.remove(userName);
        }
    }


    public static String random(int size) {
        StringBuilder generatedToken = new StringBuilder();
        try {
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            for (int i = 0; i < size; i++) {
                generatedToken.append(number.nextInt(9));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedToken.toString();
    }

    public static boolean authenticate() {
        boolean isValidUser = false;
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is your product code?");
        String productCode = scanner.next().toUpperCase();
        if (productCode == null || productCode.trim().length() != 3) {
            return isValidUser;
        }
        pCode = productCode.trim();
        Cache.setProductCode(pCode);
        Util.getEnvironmentsForProduct(pCode);
        List<String> intEmails = Cache.getIntgEmails();
        if (intEmails.isEmpty()) {
            System.out.println("MITRA is not configured for " + pCode + ". Exiting...");
            return isValidUser;
        }
        Collections.sort(intEmails);
        StringBuffer intEmailsOptions = new StringBuffer();
        int i = 1;
        for (String intEmail : intEmails) {
            if (i != 1) {
                intEmailsOptions.append("\n");
            }
            intEmailsOptions.append(i + "." + intEmail);
            i++;
        }
        String takeEmail =
            "Please select your SSO username from the below options? or Enter 9 if it is not given below\n" +
            intEmailsOptions;

        String userNameEx = null;
        ;

        boolean proceed = false;
        while (!proceed) {
            try {
                System.out.println(takeEmail);
                String intOption = scanner.next();
                int option = Integer.valueOf(intOption.trim());
                if (option == 9) {
                    System.out.println("You are not authorized to launch MITRA for " + pCode + ". Exiting...");
                    return isValidUser;
                }

                userNameEx = intEmails.get(option - 1);
                if (userNameEx != null) {
                    proceed = true;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");

            }
        }


        try {
            MultiUserClient.initializeConnection(USER_NAME_MITRA, USER_WORD_MITRA, pCode);
            String otp = random(4);
            MultiUserClient.sendMessage("OTP to launch MITRA for " + pCode + " is " + otp, userNameEx);
            System.out.println("OTP is sent to " + userNameEx);
            System.out.println("Please enter the OTP :");
            String secretCode = scanner.next();
            if (!otp.equals(secretCode)) {
                System.out.println("Authentication Failed, Exiting..");
            } else {
                System.out.println("Authentication Successful!");
                isValidUser = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValidUser;
    }
}
