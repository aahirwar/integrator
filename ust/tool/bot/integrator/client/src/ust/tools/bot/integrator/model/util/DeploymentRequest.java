package ust.tools.bot.integrator.model.util;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DeploymentRequest {

    private Long requestId;
    private String txnName;
    private Set<String> deployTypes = new HashSet<String>();
    private Set<String> soaComposites = new HashSet<String>();
    private String requestorEmail;
    private String deploymentStatus;
    private Environment environment;
    private String productCode;
    private String targetWindow;
    private Integer noOfDaysToBeIncluded;
    private Date endDeployDate;
    private Long parentRequestId;


    public void setTxnName(String txnName) {
        this.txnName = txnName;
    }

    public String getTxnName() {
        return txnName;
    }

    public void setRequestorEmail(String requestorEmail) {
        this.requestorEmail = requestorEmail;
    }

    public String getRequestorEmail() {
        return requestorEmail;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }


    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public Set<String> getDeployTypes() {
        return deployTypes;
    }

    public Set<String> getSoaComposites() {
        return soaComposites;
    }

    public void setTargetWindow(String targetWindow) {
        this.targetWindow = targetWindow;
    }

    public String getTargetWindow() {
        return targetWindow;
    }


    public String getDeploymentSummary() {
        StringBuffer summary = new StringBuffer();

        if (getTxnName() != null) {
            summary.append("Transaction Name : " + getTxnName());
        }
        if (getEnvironment() != null) {
            summary.append(",\tEnvironment Name : " + this.getEnvironment().getEnvironmentName());
        }
        Set<String> depTypes = getDeployTypes();
        if (!depTypes.isEmpty()) {
            String deployDetails = depTypes
                                       .toString()
                                       .replaceAll("\\[", "")
                                       .replaceAll("\\]", "");
            if (depTypes.contains("SOA")) {
                deployDetails = deployDetails.replaceAll("SOA", "SOA " + getSoaComposites().toString());
            }
            summary.append(",\tDeployment Type : " + deployDetails);
        }
        Date endDate = this.getEndDeployDate();
        if (endDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM");
            String date = dateFormat.format(endDate);
            summary.append(",\tDeploy Until : " + date);
        } else {
            summary.append(",\tDeploy only in the Next Window");
        }
        return summary.toString();
    }

    public String getDeploymentRequestAsString() {
        StringBuffer reqAsString = new StringBuffer(getTxnName());
        Set<String> depTypes = getDeployTypes();
        if (!depTypes.isEmpty()) {
            String deployTypes = depTypes.toString();
            if (!soaComposites.isEmpty()) {
                deployTypes = deployTypes.replaceAll("SOA", "SOA" + soaComposites.toString());
            }
            reqAsString.append(deployTypes);
        }
        return reqAsString.toString();
    }


    public void setEndDeployDate(Date endDeployDate) {
        this.endDeployDate = endDeployDate;
    }

    public Date getEndDeployDate() {
        return endDeployDate;
    }


    public void setNoOfDaysToBeIncluded(Integer noOfDaysToBeIncluded) {
        this.noOfDaysToBeIncluded = noOfDaysToBeIncluded;
    }

    public Integer getNoOfDaysToBeIncluded() {
        return noOfDaysToBeIncluded;
    }

    public void setParentRequestId(Long parentRequestId) {
        this.parentRequestId = parentRequestId;
    }

    public Long getParentRequestId() {
        return parentRequestId;
    }
}
