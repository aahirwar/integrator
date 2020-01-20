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
import ust.tools.bot.integrator.model.util.Environment;

public class ManageDeploymentsProcessor extends AbstractProcessor {

    private String envName;

    public ManageDeploymentsProcessor() {
        super();
    }


    @Override
    public void resetProcess() {
        steps.clear();
        steps.clear();
        addStep(new Step(1, Messages.SN_INTG_TASKS, Messages.SN_PROMPT_4_ENV, Messages.PROMPT_4_INTG_TASK, false));
        addStep(new Step(2, Messages.SN_PROMPT_4_ENV, Messages.SN_LOCK_VIEW, Messages.PROMPT_4_ENV, false));
        addStep(new Step(3, Messages.SN_LOCK_VIEW, Messages.SN_UNLOCK_VIEW, Messages.VIEW_LOCKED_SUCCESS, true));
        addStep(new Step(4, Messages.SN_UNLOCK_VIEW, Messages.SN_VIEW_UNLOCKED, Messages.PROMPT_4_UNLOCK_VIEW, false));
        addStep(new Step(4, Messages.SN_VIEW_UNLOCKED, null, Messages.VIEW_UNLOCKED, true));
        addStep(new Step(5, Messages.SN_REFRESH_MEMORY, null, Messages.REFRESHED_MEMORY, true));
        
        firstStep = steps.get(Messages.SN_INTG_TASKS);
        firstStep = steps.get(Messages.SN_INTG_TASKS);
        envName = null;

    }


    @Override
    public Step processStep(Step step) {

        Step nextStep = null;
        String envName = null;
        String stepName = null;

        if (step != null) {
            stepName = step.getName();
            String userInput = step.getUserInput();
            if (!Messages.SN_UNLOCK_VIEW.equals(stepName) && "9".equalsIgnoreCase(userInput)) {
                nextStep = new Step(0, Messages.SN_DUMMY, null, Messages.CLEARED, true);
            } else if (!step.isInformational()) {
                if (Messages.SN_INTG_TASKS.equals(stepName)) {
                    if ("2".equalsIgnoreCase(userInput)) {
                        step.setNextStepName(Messages.SN_REFRESH_MEMORY);
                    } else if (!"1".equalsIgnoreCase(userInput)) {
                        nextStep = step;
                    }
                } else if (Messages.SN_PROMPT_4_ENV.equals(stepName)) {
                    envName = getEnvironmentName(userInput);
                    if (envName == null) {
                        nextStep = step;
                    } else {
                        this.envName = envName;
                        Environment env = Cache.getEnvironments().get(envName);
                        env.setViewLocked(true);
                        getUserSession().setNoExpiry(true);
                    }
                } else if (Messages.SN_UNLOCK_VIEW.equals(stepName)) {
                    if (!"1".equalsIgnoreCase(userInput)) {
                        nextStep = step;
                    } else {
                        Environment env = Cache.getEnvironments().get(this.envName);
                        env.setViewLocked(false);
                        getUserSession().setNoExpiry(false);
                    }
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
                envText.append(i + 1 + "." + envs.get(envList.get(i)).getEnvironmentName() + " \t");
            }
            nextStep.setPromptMessage(Messages.PROMPT_4_ENV + envText);
        } else if (Messages.SN_REFRESH_MEMORY.equals(stepName)) {
            Cache.refresh();
        }
        return nextStep;

    }

}
