package ust.tools.bot.integrator.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Cache {

    private static String productCode = "DOS";

    private static Map<String, Environment> ENVIRONMENTS = new HashMap<String, Environment>();

    private static Queue<String> COLORS = new LinkedList<String>();

    private static List<String> INTG_EMAILS = new ArrayList<String>();

    private static String INTG_EMAILS_S = "";

    private static Map<String, Map<String, DeploymentRequest>> DEPLOY_REQUESTS =
        new HashMap<String, Map<String, DeploymentRequest>>();
    
    public static final Map<String, List<String>> INT_COMMANDS = new HashMap<String, List<String>>();


    public static final Map<String, String> TIMED_TASKS = new HashMap<String, String>();
    
    public static void loadCommands() {
        List<String> prepareViewCommands = new ArrayList<String>();
        prepareViewCommands.add(Commands.PREPARE_VIEW_CMD);
        prepareViewCommands.add(Commands.DIR_CMD);
        prepareViewCommands.add(Commands.BEGIN_TRANS_REOPEN_CMD);
        prepareViewCommands.add(Commands.BEGIN_TRANS_CMD);
        prepareViewCommands.add(Commands.EXIT_CMD);
        INT_COMMANDS.put(Constants.PREPARE_VIEW, prepareViewCommands);

        List<String> fetchTransCommands = new ArrayList<String>();
        fetchTransCommands.add(Commands.CREATE_LOK_CMD);
        fetchTransCommands.add(Commands.USEVIEW_CMD);
        fetchTransCommands.add(Commands.DIR_CMD);
        fetchTransCommands.add(Commands.SAVE_TRANS_CMD);
        fetchTransCommands.add(Commands.CREATE_LOG_CMD);
        fetchTransCommands.add(Commands.FETCH_TRANS_CMD);
        fetchTransCommands.add(Commands.ANALYSE_FETCH_CMD);
        fetchTransCommands.add(Commands.SAVE_TRANS_CMD);
        fetchTransCommands.add(Commands.CAT_LOG_CMD);
        fetchTransCommands.add(Commands.DELETE_LOK_CMD);
        fetchTransCommands.add(Commands.EXIT_CMD);
        INT_COMMANDS.put(Constants.FETCH_TRANS, fetchTransCommands);

        List<String> conflictsEmail = new ArrayList<String>();
        conflictsEmail.add(Commands.CREATE_CONFLICT_EMAIL_CMD);
        conflictsEmail.add(Commands.MAIL_CONFLICTS_CMD);
        conflictsEmail.add(Commands.EXIT_CMD);
        INT_COMMANDS.put(Constants.EMAIL_CONFLICTS, conflictsEmail);
        
        List<String> fetchedEmail = new ArrayList<String>();
        fetchedEmail.add(Commands.CREATE_FETCH_EMAIL_CMD);        
        fetchedEmail.add(Commands.MAIL_LOG_CMD);
        fetchedEmail.add(Commands.TRANS_NAMES_CMD);
        fetchedEmail.add(Commands.EXIT_CMD);
        INT_COMMANDS.put(Constants.EMAIL_FETCHED , fetchedEmail);


        List<String> notFoundEmail = new ArrayList<String>();
        notFoundEmail.add(Commands.CREATE_FETCH_EMAIL_CMD);        
        notFoundEmail.add(Commands.MAIL_LOG_CMD);
        notFoundEmail.add(Commands.EXIT_CMD);
        INT_COMMANDS.put(Constants.EMAIL_NOT_EXISTS, notFoundEmail);

        List<String> cronJobCmds = new ArrayList<String>();
        cronJobCmds.add(Commands.CREATE_CRON_CMD);
        cronJobCmds.add(Commands.CREATE_MAIL_CMD);
        cronJobCmds.add(Commands.CRON_CMD);
        //cronJobCmds.add(Commands.DELETE_CRON);
        cronJobCmds.add(Commands.EXIT_CMD);
        INT_COMMANDS.put(Constants.SCHEDULE_CRON_POST_FETCH, cronJobCmds);

        List<String> timerJobCmds = new ArrayList<String>();
        timerJobCmds.add(Commands.CREATE_CRON_CMD);
        timerJobCmds.add(Commands.CREATE_MAIL_CMD);
        timerJobCmds.add(Commands.TIMER_POST_CUTOFF);
        timerJobCmds.add(Commands.EXIT_CMD);
        INT_COMMANDS.put(Constants.SCHEDULE_TIMER_POST_FETCH, timerJobCmds);


    }



    public static List<String> getIntgEmails() {
        return INTG_EMAILS;
    }

    public static String getIntgEmailsString() {
        return INTG_EMAILS_S;
    }

    public static void setIntgEmailsString(String intgEmailsSpace) {
        INTG_EMAILS_S = intgEmailsSpace;
    }


    public static boolean refresh() {
        boolean refreshed = true;
        ENVIRONMENTS.clear();
        DEPLOY_REQUESTS.clear();
        INTG_EMAILS.clear();
        INTG_EMAILS_S = "";

        loadCommands();
        Map<String, Environment> envs = Util.getEnvironmentsForProduct(productCode);
        if (envs != null && !envs.isEmpty()) {
            ENVIRONMENTS.putAll(envs);
        } else {
            throw new RuntimeException("MITRA backend is not configured for product " + productCode);
        }
        refreshDeployRequests();
        populateColors();

        return refreshed;
    }


    public static Map<String, Environment> getEnvironments() {
        if (ENVIRONMENTS.isEmpty()) {
            refresh();
        }

        return ENVIRONMENTS;
    }

    public static void refreshDeployRequests() {
        Cache.DEPLOY_REQUESTS.clear();
        Set<String> envNames = ENVIRONMENTS.keySet();
        for (String envName : envNames) {
            Environment env = ENVIRONMENTS.get(envName);
            String targetWindow = Util.getTargetWindow(env, productCode);
            Map<String, DeploymentRequest> requests =
                Util.loadDeploymentRequests(env, productCode, "FETCHED", targetWindow);
            Cache.DEPLOY_REQUESTS.put(targetWindow, requests);
            if (requests != null && !requests.isEmpty()) {
                Cache.scheduleTimedTask(targetWindow, env);
            }


            String ptw = Util.getPreviousTargetWindow(env, productCode);
            Map<String, DeploymentRequest> deployedRequests =
                Util.loadDeploymentRequests(env, productCode, "FETCHED", ptw);
            Cache.DEPLOY_REQUESTS.put(ptw, deployedRequests);
        }
    }

    public static Map<String, DeploymentRequest> getDeployRequests(String targetWindow) {
        if (DEPLOY_REQUESTS.isEmpty() || !DEPLOY_REQUESTS.containsKey(targetWindow)) {
            refreshDeployRequests();
        }
        return DEPLOY_REQUESTS.get(targetWindow);
    }

    public static void setProductCode(String productCode) {
        Cache.productCode = productCode;
    }

    public static String getProductCode() {
        return productCode;
    }

    private static void populateColors() {
        COLORS.offer("blue");
        COLORS.offer("Brown");
        COLORS.offer("chocolate ");
    }

    public static String getColor() {
        String color = COLORS.poll();
        COLORS.offer(color);
        return color;
    }


    public static void scheduleTimedTask(String targetWindow, Environment env) {
        if(!"Y".equals(env.getBuildFlag())){
            System.out.println("Cronjob will not be set for the env : "+ env.getEnvironmentName());
        return;
        }
        Map<String, DeploymentRequest> requests = getDeployRequests(targetWindow);
        if (requests != null && !requests.isEmpty() && Cache.TIMED_TASKS.get(targetWindow) == null) {
            Util.schedulTimedTask4PostFetchScript(targetWindow, productCode, env, new StringBuffer());
            Cache.TIMED_TASKS.put(targetWindow, "TRUE");
        }
    }
}
