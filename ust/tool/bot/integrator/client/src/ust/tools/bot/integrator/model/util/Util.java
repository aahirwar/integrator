package ust.tools.bot.integrator.model.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import oracle.jbo.client.Configuration;

import org.apache.commons.lang.WordUtils;

import ust.tools.bot.integrator.model.applicationModule.IntegratorClientAMImpl;
import ust.tools.bot.integrator.model.connector.IntegratorServiceConnector;
import ust.tools.bot.integrator.model.xmpp.smack.MultiUserClient;

public class Util {

    public static final TimeZone PST_TIME_ZONE = TimeZone.getTimeZone("America/Los_Angeles");

    public static String getTargetWindow(Environment env, String pCode) {

        StringBuffer targetWindow = new StringBuffer();
        targetWindow.append(pCode);
        String envName = env.getEnvironmentName();
        String envNameModified = envName.replaceAll("_", "");
        envNameModified = envNameModified.replaceAll(" ", "");
        targetWindow.append(envNameModified);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd HH.mm");
        dateFormat.setTimeZone(PST_TIME_ZONE);
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(PST_TIME_ZONE);
        String date = dateFormat.format(cal.getTime());

        String[] dateTime = date.split(" ");
        String dayMonth = dateTime[0];

        Float reqTime = Float.valueOf(dateTime[1]);
        String windowTimesString = env.getWindowHours();
        String[] windowTimes = windowTimesString.split(",");

        String targetTime = null;
        List<Float> windowStartTimes = new ArrayList<Float>();
        for (int i = 0; i < windowTimes.length; i++) {
            String[] window = windowTimes[i].split("-");
            window[0] = window[0].replaceAll(":", ".");
            Float windowStartTime = Float.valueOf(window[0]);
            windowStartTimes.add(windowStartTime);
        }
        Collections.sort(windowStartTimes);

        Float previousStart = Float.valueOf("0.0");

        for (Float nextStart : windowStartTimes) {
            if (previousStart < reqTime && reqTime < nextStart) {
                targetTime = nextStart.toString();
                break;
            }
        }
        if (targetTime == null) {
            targetTime = windowStartTimes.get(0).toString();
            cal.add(Calendar.DATE, 1);
            Date nextDay = cal.getTime();
            date = dateFormat.format(nextDay);
            dateTime = date.split(" ");
            dayMonth = dateTime[0];
        }
        String hh = "00";
        String mm = "00";
        String[] times = targetTime.split("\\.");
        hh = times[0];
        if (times.length == 2) {
            mm = times[1];
        }
        if (hh.length() == 1) {
            hh = "0" + hh;
        }
        if (mm.length() == 1) {
            mm = mm + "0";
        }
        targetTime = hh + "." + mm;

        targetWindow.append(dayMonth + targetTime);
        return targetWindow.toString()
                           .toLowerCase()
                           .replaceAll("\\.", "");
    }


    public static String getPreviousTargetWindow(Environment env, String pCode) {

        StringBuffer targetWindow = new StringBuffer();
        targetWindow.append(pCode);
        String envName = env.getEnvironmentName();
        String envNameModified = envName.replaceAll("_", "");
        envNameModified = envNameModified.replaceAll(" ", "");
        targetWindow.append(envNameModified);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd HH.mm");
        dateFormat.setTimeZone(PST_TIME_ZONE);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(PST_TIME_ZONE);
        String date = dateFormat.format(cal.getTime());
        String[] dateTime = date.split(" ");

        String dayMonth = dateTime[0];

        Float reqTime = Float.valueOf(dateTime[1]);
        String windowTimesString = env.getWindowHours();
        String[] windowTimes = windowTimesString.split(",");

        String targetTime = null;
        List<Float> windowStartTimes = new ArrayList<Float>();
        for (int i = 0; i < windowTimes.length; i++) {
            String[] window = windowTimes[i].split("-");
            window[0] = window[0].replaceAll(":", ".");
            Float windowStartTime = Float.valueOf(window[0]);
            windowStartTimes.add(windowStartTime);
        }
        Collections.sort(windowStartTimes);
        Collections.reverse(windowStartTimes);

        Float nStart = Float.valueOf("24.0");

        for (Float pStart : windowStartTimes) {
            if (nStart >= reqTime && reqTime > pStart) {
                targetTime = pStart.toString();
                break;
            }
        }
        if (targetTime == null) {
            targetTime = windowStartTimes.get(0).toString();
            cal.add(Calendar.DATE, -1);
            Date previousDay = cal.getTime();
            date = dateFormat.format(previousDay);
            dateTime = date.split(" ");
            dayMonth = dateTime[0];
        }
        String hh = "00";
        String mm = "00";
        String[] times = targetTime.split("\\.");
        hh = times[0];
        if (times.length == 2) {
            mm = times[1];
        }
        if (hh.length() == 1) {
            hh = "0" + hh;
        }
        if (mm.length() == 1) {
            mm = mm + "0";
        }
        targetTime = hh + "." + mm;
        targetWindow.append(dayMonth + targetTime);
        return targetWindow.toString()
                           .toLowerCase()
                           .replaceAll("\\.", "");
    }

    public static String getNextWindowStartTime(Environment env) {
        StringBuffer targetWindow = new StringBuffer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMMM-HH.mm");
        dateFormat.setTimeZone(PST_TIME_ZONE);
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(PST_TIME_ZONE);
        String date = dateFormat.format(cal.getTime());

        String[] dateTime = date.split("-");

        String dayMonth = dateTime[0];

        Float reqTime = Float.valueOf(dateTime[1]);
        String windowTimesString = env.getWindowHours();
        String[] windowTimes = windowTimesString.split(",");

        String targetTime = null;
        List<Float> windowStartTimes = new ArrayList<Float>();
        for (int i = 0; i < windowTimes.length; i++) {
            String[] window = windowTimes[i].split("-");
            window[0] = window[0].replaceAll(":", ".");
            Float windowStartTime = Float.valueOf(window[0]);
            windowStartTimes.add(windowStartTime);
        }
        Collections.sort(windowStartTimes);

        Float previousStart = Float.valueOf("0.0");

        for (Float nextStart : windowStartTimes) {
            if (previousStart < reqTime && reqTime < nextStart) {
                targetTime = String.valueOf(nextStart);
                //targetTime = targetTime.replaceAll("\\.", ":");
                dayMonth = "Today";
                break;
            }
        }
        if (targetTime == null) {
            targetTime = windowStartTimes.get(0).toString();
            cal.add(Calendar.DATE, 1);
            Date nextDay = cal.getTime();
            date = dateFormat.format(nextDay);
            dateTime = date.split("-");
            dayMonth = "Tomorrow";
        }

        String hh = "00";
        String mm = "00";
        String[] times = targetTime.split("\\.");
        hh = times[0];
        if (times.length == 2) {
            mm = times[1];
        }
        if (hh.length() == 1) {
            hh = "0" + hh;
        }
        if (mm.length() == 1) {
            mm = mm + "0";
        }
        targetTime = hh + "." + mm;


        targetWindow.append(dayMonth + " @ " + targetTime);
        targetWindow.append(" Hrs PST");
        return targetWindow.toString();
    }

    public static String getNextWindowStartTimeServer(Environment env) {
        StringBuffer targetWindow = new StringBuffer();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH.mm");
        dateFormat.setTimeZone(PST_TIME_ZONE);
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(PST_TIME_ZONE);
        String date = dateFormat.format(cal.getTime());
        String[] dateTime = date.split(" ");

        String dayMonth = dateTime[0];

        Float reqTime = Float.valueOf(dateTime[1]);
        String windowTimesString = env.getWindowHours();
        String[] windowTimes = windowTimesString.split(",");

        String targetTime = null;
        List<Float> windowStartTimes = new ArrayList<Float>();
        for (int i = 0; i < windowTimes.length; i++) {
            String[] window = windowTimes[i].split("-");
            window[0] = window[0].replaceAll(":", ".");
            Float windowStartTime = Float.valueOf(window[0]);
            windowStartTimes.add(windowStartTime);
        }
        Collections.sort(windowStartTimes);

        Float previousStart = Float.valueOf("0.0");


        for (Float nextStart : windowStartTimes) {
            Float nextStartFloat = Float.valueOf(nextStart);
            if (previousStart < reqTime && reqTime < nextStartFloat) {
                targetTime = nextStart.toString();
                break;
            }
        }
        if (targetTime == null) {
            targetTime = windowStartTimes.get(0).toString();
            cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, 1);
            Date nextDay = cal.getTime();
            date = dateFormat.format(nextDay);
            dateTime = date.split(" ");
            dayMonth = dateTime[0];
        }

        String hh = "00";
        String mm = "00";
        String[] times = targetTime.split("\\.");
        hh = times[0];
        if (times.length == 2) {
            mm = times[1];
        }
        if (hh.length() == 1) {
            hh = "0" + hh;
        }
        if (mm.length() == 1) {
            mm = mm + "0";
        }
        targetTime = hh + "." + mm;
        targetWindow.append(dayMonth + targetTime);
        return targetWindow.toString()
                           .toLowerCase()
                           .replaceAll("\\.", "");
    }

    public static Map<String, DeploymentRequest> loadDeploymentRequests(Environment env, String pCode, String status,
                                                                        String targetWindow) {
        Map<String, DeploymentRequest> requests = null;
        IntegratorClientAMImpl intgAM = null;
        try {
            intgAM =
                (IntegratorClientAMImpl) Configuration.createRootApplicationModule("ust.tools.bot.integrator.model.applicationModule.IntegratorClientAM",
                                                                                   "IntegratorAMLocal");
            requests = intgAM.getDeploymentRequests(targetWindow, status);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Configuration.releaseRootApplicationModule(intgAM, false);
        }
        return requests;

    }


    public static Map<String, DeploymentRequest> updateDeploymentRequestStatus(Long requestId, String status,
                                                                               String targetWindow) {
        Map<String, DeploymentRequest> requests = null;
        IntegratorClientAMImpl intgAM = null;
        try {
            intgAM =
                (IntegratorClientAMImpl) Configuration.createRootApplicationModule("ust.tools.bot.integrator.model.applicationModule.IntegratorClientAM",
                                                                                   "IntegratorAMLocal");
            intgAM.updateDeploymentRequest(requestId, status, targetWindow);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Configuration.releaseRootApplicationModule(intgAM, false);
        }
        return requests;

    }


    public static boolean logDeploymentRequest(DeploymentRequest request) {
        boolean recorded = recordDeploymentRequest(request);
        //        if (recorded) {
        //            String envName = request.getEnvironment().getEnvironmentName();
        //            String txnName = request.getTxnName();
        //            Map<String, DeploymentRequest> requests = Cache.getEnvironmentDeployRequests().get(envName);
        //            if (requests == null) {
        //                requests = new HashMap<String, DeploymentRequest>();
        //                Cache.getEnvironmentDeployRequests().put(envName, requests);
        //            }
        //            requests.put(txnName, request);
        //        }
        return recorded;
    }


    private static boolean recordDeploymentRequest(DeploymentRequest request) {
        boolean loaded = true;
        IntegratorClientAMImpl intgAM = null;
        try {
            intgAM =
                (IntegratorClientAMImpl) Configuration.createRootApplicationModule("ust.tools.bot.integrator.model.applicationModule.IntegratorClientAM",
                                                                                   "IntegratorAMLocal");
            intgAM.logDeploymentRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
            loaded = false;
        } finally {
            Configuration.releaseRootApplicationModule(intgAM, false);
        }
        return loaded;

    }


    public static Map<String, Environment> getEnvironmentsForProduct(String pCode) {
        Map<String, Environment> envs = null;
        IntegratorClientAMImpl intgAM = null;
        try {
            intgAM =
                (IntegratorClientAMImpl) Configuration.createRootApplicationModule("ust.tools.bot.integrator.model.applicationModule.IntegratorClientAM",
                                                                                   "IntegratorAMLocal");
            envs = intgAM.getEnvironmentsForProduct(pCode);
        } catch (Exception e) {
            System.out.println("Exception Occured");
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            Configuration.releaseRootApplicationModule(intgAM, false);
        }
        return envs;

    }

    public static boolean initializeRAM() {
        boolean initialized = true;
        System.out.print("Initializing RAM...");
        IntegratorClientAMImpl intgAM = null;
        try {

            intgAM =
                (IntegratorClientAMImpl) Configuration.createRootApplicationModule("ust.tools.bot.integrator.model.applicationModule.IntegratorClientAM",
                                                                                   "IntegratorAMLocal");
            System.out.println("Initialized");
        } catch (Exception e) {
            System.out.println("Failed, seems DB is down.");
            initialized = false;
        } finally {
            if (intgAM != null) {
                Configuration.releaseRootApplicationModule(intgAM, false);
            }
        }
        return initialized;

    }


    public static long toUTC(long time, TimeZone from) {
        return convertTime(time, from, TimeZone.getTimeZone("UTC"));
    }

    public static long convertTime(long time, TimeZone from, TimeZone to) {
        return time + getTimeZoneOffset(time, from, to);
    }

    private static long getTimeZoneOffset(long time, TimeZone from, TimeZone to) {
        int fromOffset = from.getOffset(time);
        int toOffset = to.getOffset(time);
        int diff = 0;

        if (fromOffset >= 0) {
            if (toOffset > 0) {
                toOffset = -1 * toOffset;
            } else {
                toOffset = Math.abs(toOffset);
            }
            diff = (fromOffset + toOffset) * -1;
        } else {
            if (toOffset <= 0) {
                toOffset = -1 * Math.abs(toOffset);
            }
            diff = (Math.abs(fromOffset) + toOffset);
        }
        return diff;
    }


    public static void executeTimedTask(List<String> commands, Map<String, String> tokens, StringBuffer log,
                                        Date scheduleForTime) {
        Long timeInMilliSeconds = scheduleForTime.getTime() - (new Date()).getTime();


        System.out.println("Timed Task to be executed @ " + scheduleForTime + " (" +
                           TimeUnit.MILLISECONDS.toMinutes(timeInMilliSeconds) + " minutes from now). Time Now : " +
                           new Date());
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

        Runnable oneShotTask = new Runnable() {
            @Override
            public void run() {
                String targetWindow = tokens.get("viewname");
                System.out.println("Executing Timed Task. Time Now:" + new Date());
                executeCommands(commands, tokens, log);
                System.out.println("One Shot Task Executed. Time  Now:" + new Date());
                Cache.TIMED_TASKS.remove(targetWindow);
            }
        };
        executor.schedule(oneShotTask, timeInMilliSeconds, TimeUnit.MILLISECONDS);
    }


    public static void executeFetchTrans(DeploymentRequest request) {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        Runnable oneShotTask = new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (Util.class) {
                        String transName = request.getTxnName();
                        String requestorName = request.getRequestorEmail();
                        requestorName = requestorName.split("@")[0];
                        requestorName = "@" + WordUtils.capitalize(requestorName.replaceAll("\\.", " ")) + " : ";
                        String message = "Fetching Transaction " + transName;
                        message = requestorName + message;
                        MultiUserClient.announce(message);
                        message = IntegratorServiceConnector.processRequestLocal(request);
                        message = message.replaceAll("TXNNAME", transName);
                        message = requestorName + message;
                        MultiUserClient.announce(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        executor.schedule(oneShotTask, Long.valueOf(3), TimeUnit.SECONDS);
    }


    public static void executeCommands(List<String> commands, Map<String, String> tokens, StringBuffer log) {
        try {

            ProcessBuilder builder = new ProcessBuilder("/bin/bash");
            Process process = builder.start();

            BufferedWriter pi = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            Scanner s = new Scanner(process.getInputStream());
            if (tokens != null) {
                Set<String> tNames = tokens.keySet();
                System.out.println("-------- Tokens --------");
                for (String tName : tNames) {
                    String tValue = tokens.get(tName);
                    System.out.println(tName + " : " + tValue);
                }
            }
            if (commands != null) {
                for (String command : commands) {
                    if (tokens != null) {
                        Set<String> tNames = tokens.keySet();
                        for (String tName : tNames) {
                            String tValue = tokens.get(tName);
                            command = command.replaceAll(tName, tValue);
                        }
                    }
                    System.out.println(command);
                    pi.write("echo '" + command + "'");
                    pi.newLine();
                    pi.flush();

                    pi.write(command);
                    pi.newLine();
                    pi.flush();

                }
                while (s.hasNextLine()) {
                    log.append(s.nextLine());
                    log.append("\n");
                }
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void schedulTimedTask4PostFetchScript(String targetWindow, String pCode, Environment env,
                                                        StringBuffer log) {
        System.out.println("Scheduling Timed Task for Target Window " + targetWindow);

        List<String> cronCmds = Cache.INT_COMMANDS.get(Constants.SCHEDULE_TIMER_POST_FETCH);
        Map<String, String> tokens = new HashMap<String, String>();
        String startTime = Util.getNextWindowStartTimeServer(env);

        Calendar calender = Calendar.getInstance();
        calender.setTimeZone(Util.PST_TIME_ZONE);
        calender.set(Calendar.YEAR, Integer.valueOf(startTime.substring(0, 4)));
        calender.set(Calendar.MONTH, Integer.valueOf(startTime.substring(4, 6)) - 1);
        calender.set(Calendar.DAY_OF_MONTH, Integer.valueOf(startTime.substring(6, 8)));
        calender.set(Calendar.HOUR_OF_DAY, Integer.valueOf(startTime.substring(8, 10)));
        if (startTime.length() == 10) {
            calender.set(Calendar.MINUTE, 0);
        } else {
            calender.set(Calendar.MINUTE, Integer.valueOf(startTime.substring(10, startTime.length())));
        }
        if ("SMC".equalsIgnoreCase(env.getEnvironmentType())) {
            tokens.put("template", "SMC");
        } else {
            tokens.put("template", "CLASSIC");
        }
        tokens.put("mitradir", Constants.MITRA_DIR);
        tokens.put("viewname", targetWindow);
        tokens.put("pcode", pCode);
        tokens.put("environment", env.getEnvironmentName());
        String intgEmails = Cache.getIntgEmailsString();
        if (intgEmails != null && !intgEmails.isEmpty()) {
            tokens.put("intgemails", intgEmails);
        }
        Util.executeTimedTask(cronCmds, tokens, log, calender.getTime());
    }


}
