package ust.tools.bot.integrator.model.core;


public class Session {


    private AbstractProcessor processor;

    private String userName;

    private String userEmailAddress;

    private String productCode;

    private Long lastAccessedTimeInMillis;

    private boolean expired;

    private boolean warned;

    private boolean integrator;
    
    private boolean noExpiry;
    
    private String color;


    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }


    public void setUserEmailAddress(String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }

    public String getUserEmailAddress() {
        return userEmailAddress;
    }

    public void setProcessor(AbstractProcessor processor) {
        this.processor = processor;
    }

    public AbstractProcessor getProcessor() {
        return processor;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setLastAccessedTimeInMillis(Long lastAccessedTimeInMillis) {
        setWarned(false);
        this.lastAccessedTimeInMillis = lastAccessedTimeInMillis;
    }

    public Long getLastAccessedTimeInMillis() {
        return lastAccessedTimeInMillis;
    }

    public void setWarned(boolean warned) {
        this.warned = warned;
    }

    public boolean isWarned() {
        return warned;
    }

    public void setIntegrator(boolean integrator) {
        this.integrator = integrator;
    }

    public boolean isIntegrator() {
        return integrator;
    }

    public void setNoExpiry(boolean noExpiry) {
        this.noExpiry = noExpiry;
    }

    public boolean hasNoExpiry() {
        return noExpiry;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
