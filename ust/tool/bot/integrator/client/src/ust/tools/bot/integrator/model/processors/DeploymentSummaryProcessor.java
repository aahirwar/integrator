package ust.tools.bot.integrator.model.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ust.tools.bot.integrator.model.lang.Messages;
import ust.tools.bot.integrator.model.core.AbstractProcessor;
import ust.tools.bot.integrator.model.core.Step;
import ust.tools.bot.integrator.model.util.Cache;
import ust.tools.bot.integrator.model.util.DeploymentRequest;
import ust.tools.bot.integrator.model.util.Environment;
import ust.tools.bot.integrator.model.util.Util;


public class DeploymentSummaryProcessor extends AbstractProcessor {

    @Override
    public void resetProcess() {
        steps.clear();
        addStep(new Step(1, Messages.SN_PROMPT_4_ENV, Messages.SN_DEPLOYMENTS_SUMMARY, Messages.PROMPT_4_ENV_NCO,
                         false));
        addStep(new Step(2, Messages.SN_DEPLOYMENTS_SUMMARY, null, Messages.NO_DEPLOYMENTS, true));
        firstStep = steps.get(Messages.SN_PROMPT_4_ENV);
    }


    @Override
    public Step processStep(Step step) {
        String stepName = null;
        Step nextStep = null;
        String envName = null;
        if (step != null) {
            stepName = step.getName();
            String userInput = step.getUserInput();
            if (Messages.SN_PROMPT_4_ENV.equals(stepName)) {
                envName = getEnvironmentName(userInput);
                if (envName == null) {
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
            for (int i = 0; i < envList.size(); i++) {
                envText.append(i + 1 + "." + envs.get(envList.get(i)).getEnvironmentName());
                if (i +1 < envList.size()) {
                    envText.append("\n");
                }
            }
            nextStep.setPromptMessage(Messages.PROMPT_4_ENV_NCO + envText);
        } else if (Messages.SN_DEPLOYMENTS_SUMMARY.equals(stepName)) {
            Environment env = Cache.getEnvironments().get(envName);
            String targetWindow = Util.getTargetWindow(env, Cache.getProductCode());
            StringBuffer deploymentInfo = new StringBuffer();
            Map<String, DeploymentRequest> requests = Cache.getDeployRequests(targetWindow);
            if (!requests.isEmpty()) {
                StringBuffer depDetails = new StringBuffer();
                Set<String> txnNames = requests.keySet();
                for (String txnName : txnNames) {
                    DeploymentRequest request = requests.get(txnName);
                    depDetails.append(request.getDeploymentRequestAsString());
                    depDetails.append("\n");
                }
                deploymentInfo.append("\nRequests for next deployment window ( " + targetWindow + " ) :\n" + depDetails);
            }
            String ptw = Util.getPreviousTargetWindow(env, Cache.getProductCode());
            Map<String, DeploymentRequest> depReqs = Cache.getDeployRequests(ptw);
            if (!depReqs.isEmpty()) {
                StringBuffer deployedDetails = new StringBuffer();
                Set<String> txnNames = depReqs.keySet();
                for (String txnName : txnNames) {
                    DeploymentRequest request = depReqs.get(txnName);
                    deployedDetails.append(request.getDeploymentRequestAsString());
                    deployedDetails.append("\n");
                }
                deploymentInfo.append("\nRequests included in the previous deployment window ( " + ptw + " ) :\n" +
                                      deployedDetails);
            }
            if (!deploymentInfo.toString().isEmpty()) {
                nextStep.setPromptMessage(Messages.DEPLOYMENT_SUMMARY + deploymentInfo.toString());
            }
        }
        return nextStep;
    }
}
