package ust.tools.bot.integrator.model.processors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import ust.tools.bot.integrator.model.core.AbstractProcessor;
import ust.tools.bot.integrator.model.core.Step;
import ust.tools.bot.integrator.model.lang.Messages;
import ust.tools.bot.integrator.model.util.Cache;
import ust.tools.bot.integrator.model.util.DeploymentRequest;
import ust.tools.bot.integrator.model.util.Environment;
import ust.tools.bot.integrator.model.util.Util;

public class DeploymentRequestProcessor extends AbstractProcessor {

    private static String[] deploymentTypes = new String[] { "ADF", "SOA", "DB" };

    private DeploymentRequest deployRequest = new DeploymentRequest();
    
    public DeploymentRequestProcessor() {
        resetProcess();
    }

    public void resetProcess() {
        steps.clear();
        addStep(new Step(1, Messages.SN_PROMT_4_TXN, Messages.SN_PROMPT_4_ENV, Messages.PROMPT_4_TXN, false));
        addStep(new Step(2, Messages.SN_PROMPT_4_ENV, Messages.SN_PROMPT_4_DEPLOY_TYPE, Messages.PROMPT_4_ENV, false));
//        addStep(new Step(3, Messages.SN_PROMPT_4_DEPLOY_TYPE, Messages.SN_PROMPT_4_END_DEPLOY_DATE,
//                         Messages.PROMPT_4_DEPLOY_TYPE, false));
                addStep(new Step(3, Messages.SN_PROMPT_4_DEPLOY_TYPE, Messages.SN_DEPLOY_REQUEST_SUMMARY,
                         Messages.PROMPT_4_DEPLOY_TYPE, false));
                
        addStep(new Step(4, Messages.SN_PROMPT_4_SOA_COMP_DTLS, Messages.SN_PROMPT_4_SOA_COMPS,
                         Messages.PROMPT_4_SOA_COMPS, false));
        
        addStep(new Step(5, Messages.SN_PROMPT_4_SOA_COMPS, Messages.SN_DEPLOY_REQUEST_SUMMARY,
                         Messages.PROMPT_4_SOA_COMPOSITES, false));
        
//        addStep(new Step(6, Messages.SN_PROMPT_4_END_DEPLOY_DATE, Messages.SN_DEPLOY_REQUEST_SUMMARY,
//                         Messages.PROMPT_4_END_DEPLOY_DATE, false));
        
        addStep(new Step(7, Messages.SN_DEPLOY_REQUEST_SUMMARY, Messages.SN_PROCESS_REQUEST,
                         Messages.DEPLOY_REQUEST_SUMMARY, false));
        addStep(new Step(8, Messages.SN_PROCESS_REQUEST, null, Messages.SUBMITED_REQUEST, true));
        //addStep(new Step(9, Messages.SN_PROCESS_STATUS, null, Messages.SUBMITED_REQUEST, true));
        addStep(new Step(10, Messages.SN_VIEW_LOCKED, null, Messages.VIEW_LOCKED, true));
        deployRequest = new DeploymentRequest();
    }


    public Step processStep(Step step) {
        String stepName = null;
        Step nextStep = null;
        if (step != null) {
            String userInput = step.getUserInput();
            stepName = step.getName();
            if ("9".equalsIgnoreCase(userInput)) {
                nextStep = new Step(0, Messages.SN_DUMMY, null, Messages.CLEARED, true);
            } else if (Messages.SN_PROMPT_4_ENV.equals(stepName)) {
                setEnvironmentName(userInput);
                if (deployRequest.getEnvironment() == null) {
                    nextStep = step;
                } else if (deployRequest.getEnvironment().isViewLocked()) {
                    step.setNextStepName(Messages.SN_VIEW_LOCKED);
                } else {
                    Environment env = deployRequest.getEnvironment();
                    if (Environment.TYPE_SMC.equalsIgnoreCase(env.getEnvironmentType())) {
                        step.setNextStepName(Messages.SN_DEPLOY_REQUEST_SUMMARY);
                    }
                }
            } else if (Messages.SN_PROMPT_4_DEPLOY_TYPE.equals(stepName)) {
                setDeploymentType(userInput);
                if (deployRequest.getDeployTypes().isEmpty()) {
                    nextStep = step;
                } else if (deployRequest.getDeployTypes().contains("SOA")) {
                    step.setNextStepName(Messages.SN_PROMPT_4_SOA_COMP_DTLS);
                }
            } else if (Messages.SN_PROMT_4_TXN.equals(stepName)) {
                setTransactionName(userInput);
                if (deployRequest.getTxnName() == null) {
                    nextStep = step;
                }
            } else if (Messages.SN_DEPLOY_REQUEST_SUMMARY.equals(stepName)) {
                
                if ("2".equalsIgnoreCase(userInput)) {
                    resetProcess();
                    nextStep = firstStep;
                } else if (!"1".equalsIgnoreCase(userInput)) {
                    nextStep = step;
                } else {
                    System.out.println("Requester Email :" + this.getUserSession().getUserEmailAddress());
                    deployRequest.setRequestorEmail(this.getUserSession().getUserEmailAddress());
                    deployRequest.setProductCode(this.getUserSession().getProductCode());
                    boolean recorded = Util.logDeploymentRequest(deployRequest);
                    if (recorded) {
                        Util.executeFetchTrans(deployRequest) ;
                    }
                }
            } else if (Messages.SN_PROMPT_4_SOA_COMP_DTLS.equals(stepName)) {
                if ("1".equalsIgnoreCase(userInput)) {
                    setSoaComposites("All-Composites");
                    step.setNextStepName(Messages.SN_PROMPT_4_END_DEPLOY_DATE);
                } else if (!"2".equalsIgnoreCase(userInput)) {
                    nextStep = step;
                }
            } else if (Messages.SN_PROMPT_4_SOA_COMPS.equals(stepName)) {
                setSoaComposites(userInput);
                if (deployRequest.getSoaComposites().isEmpty()) {
                    nextStep = step;
                }
            } else if (Messages.SN_PROMPT_4_END_DEPLOY_DATE.equals(stepName)) {
                setEndDeployDate(userInput);
                if (deployRequest.getNoOfDaysToBeIncluded() == null) {
                    nextStep = step;
                }
            } 
        }

        if (nextStep == null) {
            nextStep = getNextStep(step);
        }
        stepName = nextStep.getName();

        if (Messages.SN_PROMPT_4_ENV.equals(stepName)) {
            StringBuffer envText = new StringBuffer("\n");
            Map<String, Environment> envs = Cache.getEnvironments();
            Set<String> envNames = envs.keySet();
            List<String> envList = new ArrayList<String>();
            envList.addAll(envNames);
            Collections.sort(envList);
            int noOfEnvs = envList.size();
            for (int i = 0; i < noOfEnvs; i++) {
                String startTime = Util.getNextWindowStartTime(envs.get(envList.get(i)));
                envText.append(i + 1 + "." + envs.get(envList.get(i)).getEnvironmentName() + " (Cut-off Time for Next Window : " +
                               startTime + ")");
                if (i + 1 < noOfEnvs) {
                    envText.append("\n");
                }
            }
            nextStep.setPromptMessage(Messages.PROMPT_4_ENV + envText);
        } else if (Messages.SN_PROMPT_4_DEPLOY_TYPE.equals(stepName)) {
            StringBuffer depTypeText = new StringBuffer("\n");
            for (int i = 0; i < deploymentTypes.length; i++) {
                depTypeText.append(i + 1 + "." + deploymentTypes[i] + " \t");
            }
            nextStep.setPromptMessage(Messages.PROMPT_4_DEPLOY_TYPE + depTypeText.toString());
        } else if (Messages.SN_DEPLOY_REQUEST_SUMMARY.equals(stepName)) {
            String summary = deployRequest.getDeploymentSummary();
            nextStep.setPromptMessage(Messages.DEPLOY_REQUEST_SUMMARY + summary + Messages.CONFIRM_DIALOGUE);
        }
        return nextStep;
    }

    public void setSoaComposites(String soaComposites) {
        String[] composites = soaComposites.split(",");
        for (int i = 0; i < composites.length; i++) {
            composites[i] = composites[i].trim();
            if (composites[i].indexOf(" ") > -1) {
                deployRequest.getSoaComposites().clear();
                return;
            } else {
                deployRequest.getSoaComposites().add(composites[i]);
            }
        }
    }


    private void setEnvironmentName(String envOption) {
        try {
            Map<String, Environment> envs = Cache.getEnvironments();
            Set<String> envNames = envs.keySet();
            List<String> envList = new ArrayList<String>();
            envList.addAll(envNames);
            Collections.sort(envList);
            this.deployRequest.setEnvironment(envs.get(envList.get(Integer.valueOf(envOption) - 1)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setTransactionName(String transactionName) {
        transactionName = transactionName.trim().toLowerCase();
        if (transactionName.indexOf(" ") > -1) {
            return;
        }
        this.deployRequest.setTxnName(transactionName);
    }

    private void setDeploymentType(String depOption) {
        try {
            String[] depOptions = depOption.split(",");
            for (int i = 0; i < depOptions.length; i++) {
                depOptions[i] = depOptions[i].trim();
                this.deployRequest
                    .getDeployTypes()
                    .add(deploymentTypes[Integer.valueOf(depOptions[i]) - 1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.deployRequest
                .getDeployTypes()
                .clear();
        }
    }


    private void setEndDeployDate(String userInput) {
        userInput = userInput.trim();
        try {
            int noOfDays = Integer.valueOf(userInput);
            if (noOfDays > -1 && noOfDays < 8) {
                deployRequest.setNoOfDaysToBeIncluded(noOfDays);
                if (noOfDays != 0) {
                    TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
                    Calendar cal = Calendar.getInstance(tz);
                    cal.setTime(new Date());
                    cal.add(Calendar.DATE, noOfDays);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Date endDeployDate = cal.getTime();
                    deployRequest.setEndDeployDate(endDeployDate);
                } else {
                    deployRequest.setEndDeployDate(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
