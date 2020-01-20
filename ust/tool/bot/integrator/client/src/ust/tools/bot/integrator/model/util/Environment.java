package ust.tools.bot.integrator.model.util;


public class Environment {
    public static final String TYPE_SMC = "SMC";
    public static final String TYPE_CLASSIC = "CLASSIC";
    private Long environmentId;
    private String environmentName;
    private String environmentType;
    private String windowHours;
    private String adeBranch;

    private String buildFlag;
    private boolean viewLocked;


    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
    }

    public String getEnvironmentType() {
        return environmentType;
    }

    public void setWindowHours(String windowHours) {
        this.windowHours = windowHours;
    }

    public String getWindowHours() {
        return windowHours;
    }

    public void setBuildFlag(String buildFlag) {
        this.buildFlag = buildFlag;
    }

    public String getBuildFlag() {
        return buildFlag;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setAdeBranch(String adeBranch) {
        this.adeBranch = adeBranch;
    }

    public String getAdeBranch() {
        return adeBranch;
    }

    public void setViewLocked(boolean lockedView) {
        this.viewLocked = lockedView;
    }

    public boolean isViewLocked() {
        return viewLocked;
    }
}
