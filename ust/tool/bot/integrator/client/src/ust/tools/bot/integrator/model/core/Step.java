package ust.tools.bot.integrator.model.core;


public class Step {

    public Step(int stepNumber, String stepName, String nextStepName, String stepMessage, boolean infoOnly) {
        this.stepNumber = stepNumber;
        this.name = stepName;
        this.nextStepName = nextStepName;
        this.promptMessage = stepMessage;
        this.informational = infoOnly;
    }

    private int stepNumber;

    private String name;

    String expectedAttribute;

    boolean informational;

    boolean lastStep;

    private String nextStepName;
    
    private String previousStepName;

    private String promptMessage;

    private String userInput;

    public void setNextStepName(String nextStep) {
        this.nextStepName = nextStep;
    }

    public String getNextStepName() {
        return nextStepName;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isLastStep() {
        return nextStepName == null;
    }


    public void setInformational(boolean informational) {
        this.informational = informational;
    }

    public boolean isInformational() {
        return informational;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public void setPromptMessage(String promptMessage) {
        this.promptMessage = promptMessage;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getUserInput() {
        if (userInput != null) {
            userInput = userInput.trim();
        }
        return userInput;
    }

    public void setExpectedAttribute(String expectedAttribute) {
        this.expectedAttribute = expectedAttribute;
    }

    public String getExpectedAttribute() {
        return expectedAttribute;
    }


    public int getStepNumber() {
        return stepNumber;
    }

    public String getPromptMessage() {
        return promptMessage;
    }

    public void setPreviousStepName(String previousStepName) {
        this.previousStepName = previousStepName;
    }

    public String getPreviousStepName() {
        return previousStepName;
    }
}
