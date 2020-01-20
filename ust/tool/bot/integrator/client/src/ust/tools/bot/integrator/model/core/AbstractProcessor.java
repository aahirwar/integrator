package ust.tools.bot.integrator.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ust.tools.bot.integrator.model.util.Cache;
import ust.tools.bot.integrator.model.util.Environment;

public abstract class AbstractProcessor {
    
    public AbstractProcessor() {
        resetProcess();
    }

    protected Map<String, Step> steps = new HashMap<String, Step>();

    protected Step firstStep;
    
    protected Step previousStep;

    private Step currentStep;

    private Session userSession;
    
    
    public abstract Step processStep(Step step);
    
    public abstract void resetProcess();
    
    public void setCurrentStep(Step currentStep) {
        this.currentStep = currentStep;
    }

    public Step getCurrentStep() {
        return currentStep;
    }


    public void setUserSession(Session userSession) {
        this.userSession = userSession;
    }

    public Session getUserSession() {
        return userSession;
    }




    public void addStep(Step step) {
        if (firstStep == null) {
            firstStep = step;
        }
        
        steps.put(step.getName(), step);
    }

    public Step getNextStep(Step step) {
        Step nextStep = step;
        if (step == null) {
            nextStep = firstStep;
        } else if (step.isLastStep()) {
            nextStep = null;
        } else {
            String nextStepName = step.getNextStepName();
            if (nextStepName != null) {
                nextStep = steps.get(nextStepName);
            }
        }
        return nextStep;
    }
    
    
    protected String getEnvironmentName(String envOption) {
        String envName = null;
        try {
            String pCode = this.getUserSession().getProductCode();
            Map<String, Environment> envs = Cache.getEnvironments();
            Set<String> envNames = envs.keySet();
            List<String> envList = new ArrayList<String>();
            envList.addAll(envNames);
            Collections.sort(envList);
            envName = envList.get(Integer.valueOf(envOption) - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return envName;
    }


    public void setPreviousStep(Step previousStep) {
        this.previousStep = previousStep;
    }

    public Step getPreviousStep() {
        return previousStep;
    }
}
