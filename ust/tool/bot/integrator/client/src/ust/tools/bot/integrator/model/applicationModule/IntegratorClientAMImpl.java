package ust.tools.bot.integrator.model.applicationModule;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.jbo.ViewCriteria;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.uniqueid.UniqueIdHelper;

import ust.tools.bot.integrator.model.util.Cache;
import ust.tools.bot.integrator.model.util.DeploymentRequest;
import ust.tools.bot.integrator.model.util.Environment;
import ust.tools.bot.integrator.model.util.Util;
import ust.tools.bot.integrator.model.view.IntgDeploymentRequestsVOImpl;
import ust.tools.bot.integrator.model.view.IntgDeploymentRequestsVORowImpl;
import ust.tools.bot.integrator.model.view.IntgEnvironmentsVOImpl;
import ust.tools.bot.integrator.model.view.IntgEnvironmentsVORowImpl;
import ust.tools.bot.integrator.model.view.IntgProductsVOImpl;
import ust.tools.bot.integrator.model.view.IntgProductsVORowImpl;


// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Apr 06 16:54:19 IST 2017
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class IntegratorClientAMImpl extends ApplicationModuleImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public IntegratorClientAMImpl() {
    }

    /**
     * Container's getter for IntgDeploymentRequestsVO.
     * @return IntgDeploymentRequestsVO
     */
    public IntgDeploymentRequestsVOImpl getIntgDeploymentRequestsVO() {
        return (IntgDeploymentRequestsVOImpl) findViewObject("IntgDeploymentRequestsVO");
    }

    /**
     * Container's getter for IntgEnvironmentsVO.
     * @return IntgEnvironmentsVO
     */
    public IntgEnvironmentsVOImpl getIntgEnvironmentsVO() {
        return (IntgEnvironmentsVOImpl) findViewObject("IntgEnvironmentsVO");
    }

    /**
     * Container's getter for IntgProductsVO.
     * @return IntgProductsVO
     */
    public IntgProductsVOImpl getIntgProductsVO() {
        return (IntgProductsVOImpl) findViewObject("IntgProductsVO");
    }


    public Map<String, Environment> getEnvironmentsForProduct(String productCode) {
        Map<String, Environment> envs = new HashMap<String, Environment>();
        IntgProductsVOImpl productsVO = this.getIntgProductsVO();
        ViewCriteria criteria = productsVO.getViewCriteriaManager().getViewCriteria("ByProductCode");
        productsVO.applyViewCriteria(criteria);
        if (productCode != null) {
            productsVO.setbindProductCode(productCode);
        }
        String[] envIdsArray = null;
        productsVO.executeQuery();
        while (productsVO.hasNext()) {
            IntgProductsVORowImpl pRow = (IntgProductsVORowImpl) productsVO.next();

            String intgEmailsString = pRow.getIntegratorEmailIds();
            if (intgEmailsString != null && !intgEmailsString.isEmpty()) {
                String[] intgEmails = intgEmailsString.split(",");
                for (int i = 0; i < intgEmails.length; i++) {
                    Cache.getIntgEmails().add(intgEmails[i].trim().toLowerCase());
                }
                String emailIDs = pRow.getIntegratorEmailIds();
                Cache.setIntgEmailsString(emailIDs);

                String envIds = pRow.getEnvironmentIds();
                if (envIds != null) {
                    envIdsArray = envIds.split(",");
                }
            }
        }

        if (envIdsArray != null) {
            for (int i = 0; i < envIdsArray.length; i++) {
                Long envId = Long.valueOf(envIdsArray[i]);
                Environment env = this.getEnvironment(envId);
                if (env != null) {
                    envs.put(env.getEnvironmentName(), env);
                }
            }
        }

        return envs;
    }

    public Environment getEnvironment(Long envId) {
        Environment env = null;
        IntgEnvironmentsVOImpl envVO = this.getIntgEnvironmentsVO();
        ViewCriteria criteria = envVO.getViewCriteriaManager().getViewCriteria("ByEnvironmentId");
        envVO.applyViewCriteria(criteria);
        if (envId != null) {
            envVO.setbingEnvironmentId(envId);
        }
        envVO.executeQuery();
        while (envVO.hasNext()) {
            IntgEnvironmentsVORowImpl eRow = (IntgEnvironmentsVORowImpl) envVO.next();
            env = new Environment();
            env.setEnvironmentName(eRow.getEnvironmentName());
            env.setEnvironmentType(eRow.getEnvironmentType());
            env.setWindowHours(eRow.getWindowHours());
            env.setEnvironmentId(eRow.getEnvironmentId());
            env.setAdeBranch(eRow.getAdeBranch());
            env.setBuildFlag(eRow.getBuildFlag());
        }
        return env;
    }


    public List getIntegratorsForProduct(String productCode) {
        List<String> integs = null;
        IntgProductsVOImpl productsVO = this.getIntgProductsVO();
        ViewCriteria criteria = productsVO.getViewCriteriaManager().getViewCriteria("ByProductCode");
        productsVO.applyViewCriteria(criteria);
        if (productCode != null) {
            productsVO.setbindProductCode(productCode);
        }
        String[] envIdsArray = null;
        productsVO.executeQuery();
        while (productsVO.hasNext()) {
            IntgProductsVORowImpl pRow = (IntgProductsVORowImpl) productsVO.next();
            String envIds = pRow.getIntegratorEmailIds();
            if (envIds != null) {
                envIdsArray = envIds.split(",");
            }
        }
        if (envIdsArray != null) {
            integs = Arrays.asList(envIdsArray);
        }

        return integs;
    }

    public boolean logDeploymentRequest(DeploymentRequest request) {
        boolean logged = true;
        try {
            IntgDeploymentRequestsVOImpl deployRequestVO = this.getIntgDeploymentRequestsVO();
            IntgDeploymentRequestsVORowImpl depRequestRow = null;
            //            IntgDeploymentRequestsVOImpl requestsVO = this.getIntgDeploymentRequestsVO();
            //            ViewCriteria criteria = requestsVO.getViewCriteriaManager().getViewCriteria("ByDeploymentCriteria");
            //            requestsVO.applyViewCriteria(criteria);
            //            requestsVO.setBindTargetWindow(request.getTargetWindow());
            //            requestsVO.setBindTxnName(request.getTxnName());
            //            System.out.println(requestsVO.getQuery());
            //            requestsVO.executeQuery();
            //            requestsVO.reset();
            //
            //            while (requestsVO.hasNext()) {
            //                depRequestRow = (IntgDeploymentRequestsVORowImpl) requestsVO.next();
            //                request.setRequestId(depRequestRow.getRequestId());
            //                break;
            //            }
            if (depRequestRow == null) {
                depRequestRow = (IntgDeploymentRequestsVORowImpl) deployRequestVO.createRow();
                request.setRequestId(UniqueIdHelper.getNextId().longValue());
            }
            Calendar cal = Calendar.getInstance(Util.PST_TIME_ZONE);
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(cal.getTime().getTime());
            depRequestRow.setRequestId(request.getRequestId());
            depRequestRow.setCreationDate(currentTimestamp);
            depRequestRow.setDeploymentStatus("REQUESTED");
            depRequestRow.setEnvironmentId(request.getEnvironment().getEnvironmentId());
            depRequestRow.setProductCode(request.getProductCode());
            depRequestRow.setRequestorEmail(request.getRequestorEmail());
            depRequestRow.setTxnName(request.getTxnName());
            depRequestRow.setTargetWindow(request.getTargetWindow());
            Date untilWhen = request.getEndDeployDate();
            if (untilWhen != null) {
                depRequestRow.setEndDeployDate(new java.sql.Timestamp(untilWhen.getTime()));
            } 
            Set<String> depTypes = request.getDeployTypes();
            String depTypesString = depTypes.toString();
            depTypesString = depTypesString.substring(1, depTypesString.length() - 1);
            depRequestRow.setDeployTypes(depTypesString);

            Set<String> soaComps = request.getSoaComposites();
            if (!soaComps.isEmpty()) {
                String soaCompString = soaComps.toString();
                soaCompString = soaCompString.substring(1, soaCompString.length() - 1);
                depRequestRow.setSoaComposites(soaCompString);
            } else {
                depRequestRow.setSoaComposites(null);
            }
            deployRequestVO.insertRow(depRequestRow);
            this.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
            logged = false;
        }

        return logged;
    }

    public Map<String, DeploymentRequest> getDeploymentRequests(String targetWindow) {
        Map<String, DeploymentRequest> requests = new HashMap<String, DeploymentRequest>();
        IntgDeploymentRequestsVOImpl requestsVO = this.getIntgDeploymentRequestsVO();
        ViewCriteria criteria = requestsVO.getViewCriteriaManager().getViewCriteria("ByTargetWindow");
        requestsVO.applyViewCriteria(criteria);
        requestsVO.setBindTargetWindow(targetWindow);
        requestsVO.executeQuery();
        while (requestsVO.hasNext()) {
            IntgDeploymentRequestsVORowImpl reqRow = (IntgDeploymentRequestsVORowImpl) requestsVO.next();
            DeploymentRequest request = new DeploymentRequest();
            request.setDeploymentStatus(reqRow.getDeploymentStatus());
            request.setProductCode(reqRow.getProductCode());
            request.setRequestId(reqRow.getRequestId());
            request.setRequestorEmail(reqRow.getRequestorEmail());
            request.setTargetWindow(reqRow.getTargetWindow());
            request.setTxnName(reqRow.getTxnName());
            String txnType = reqRow.getDeployTypes();
            if (txnType != null) {
                String[] depTypes = txnType.split(",");
                for (int i = 0; i < depTypes.length; i++) {
                    request.getDeployTypes().add(depTypes[i].trim());
                }
            }
            String soaCompositesString = reqRow.getSoaComposites();
            if (soaCompositesString != null) {
                String[] soaComposites = soaCompositesString.split(",");
                for (int i = 0; i < soaComposites.length; i++) {
                    request.getSoaComposites().add(soaComposites[i]);
                }
            }
            Long envId = reqRow.getEnvironmentId();
            Environment env = getEnvironment(envId);
            request.setEnvironment(env);
            requests.put(request.getTxnName(), request);
        }
        return requests;
    }

    public Map<String, DeploymentRequest> getDeploymentRequests(String targetWindow, String status) {
        Map<String, DeploymentRequest> requests = new HashMap<String, DeploymentRequest>();
        IntgDeploymentRequestsVOImpl requestsVO = this.getIntgDeploymentRequestsVO();
        ViewCriteria criteria = requestsVO.getViewCriteriaManager().getViewCriteria("ByTargetWindowAndStatus");
        requestsVO.applyViewCriteria(criteria);
        requestsVO.setBindTargetWindow(targetWindow);
        requestsVO.setBindStatus(status);
        requestsVO.executeQuery();
        while (requestsVO.hasNext()) {
            IntgDeploymentRequestsVORowImpl reqRow = (IntgDeploymentRequestsVORowImpl) requestsVO.next();
            DeploymentRequest request = new DeploymentRequest();
            request.setDeploymentStatus(reqRow.getDeploymentStatus());
            request.setProductCode(reqRow.getProductCode());
            request.setRequestId(reqRow.getRequestId());
            request.setRequestorEmail(reqRow.getRequestorEmail());
            request.setTargetWindow(reqRow.getTargetWindow());
            request.setTxnName(reqRow.getTxnName());
            String txnType = reqRow.getDeployTypes();
            if (txnType != null) {
                String[] depTypes = txnType.split(",");
                for (int i = 0; i < depTypes.length; i++) {
                    request.getDeployTypes().add(depTypes[i].trim());
                }
            }
            String soaCompositesString = reqRow.getSoaComposites();
            if (soaCompositesString != null) {
                String[] soaComposites = soaCompositesString.split(",");
                for (int i = 0; i < soaComposites.length; i++) {
                    request.getSoaComposites().add(soaComposites[i]);
                }
            }
            Long envId = reqRow.getEnvironmentId();
            Environment env = getEnvironment(envId);
            request.setEnvironment(env);
            requests.put(request.getTxnName(), request);
        }
        return requests;
    }

    public DeploymentRequest getDeploymentRequest(Long requestId) {
        DeploymentRequest request = null;
        IntgDeploymentRequestsVOImpl requestsVO = this.getIntgDeploymentRequestsVO();
        ViewCriteria criteria = requestsVO.getViewCriteriaManager().getViewCriteria("ByRequestId");
        requestsVO.applyViewCriteria(criteria);
        requestsVO.setBindRequestId(requestId);
        requestsVO.executeQuery();
        while (requestsVO.hasNext()) {
            IntgDeploymentRequestsVORowImpl reqRow = (IntgDeploymentRequestsVORowImpl) requestsVO.next();
            request = new DeploymentRequest();
            request.setDeploymentStatus(reqRow.getDeploymentStatus());
            request.setProductCode(reqRow.getProductCode());
            request.setRequestId(reqRow.getRequestId());
            request.setRequestorEmail(reqRow.getRequestorEmail());
            request.setTargetWindow(reqRow.getTargetWindow());
            request.setTxnName(reqRow.getTxnName());
            String txnType = reqRow.getDeployTypes();
            if (txnType != null) {
                String[] depTypes = txnType.split(",");
                for (int i = 0; i < depTypes.length; i++) {
                    request.getDeployTypes().add(depTypes[i]);
                }
            }
            String soaCompositesString = reqRow.getSoaComposites();
            if (soaCompositesString != null) {
                String[] soaComposites = soaCompositesString.split(",");
                for (int i = 0; i < soaComposites.length; i++) {
                    request.getSoaComposites().add(soaComposites[i]);
                }
            }
            Long envId = reqRow.getEnvironmentId();
            Environment env = getEnvironment(envId);
            request.setEnvironment(env);
        }
        return request;
    }

    public boolean updateDeploymentRequest(Long requestId, String status, String targetWindow) {
        boolean updated = false;
        ;
        IntgDeploymentRequestsVOImpl requestsVO = this.getIntgDeploymentRequestsVO();
        ViewCriteria criteria = requestsVO.getViewCriteriaManager().getViewCriteria("ByRequestId");
        requestsVO.applyViewCriteria(criteria);
        requestsVO.setBindRequestId(requestId);
        requestsVO.executeQuery();
        while (requestsVO.hasNext()) {
            IntgDeploymentRequestsVORowImpl reqRow = (IntgDeploymentRequestsVORowImpl) requestsVO.next();
            reqRow.setDeploymentStatus(status);
            if (targetWindow != null) {
                reqRow.setTargetWindow(targetWindow);
            }
        }
        this.getTransaction().commit();
        return updated;
    }
    
    public boolean logDeploymentRequestIfDoesNotExists(DeploymentRequest request) {
        boolean logged = true;
        try {
            IntgDeploymentRequestsVORowImpl depRequestRow = null;
            IntgDeploymentRequestsVOImpl deployRequestVO =
                this.getIntgDeploymentRequestsVO();
            IntgDeploymentRequestsVOImpl requestsVO =
                this.getIntgDeploymentRequestsVO();
            ViewCriteria criteria =
                requestsVO.getViewCriteriaManager().getViewCriteria("ByDeploymentCriteria");
            criteria.resetCriteria();
            requestsVO.applyViewCriteria(criteria);
            requestsVO.setBindTargetWindow(request.getTargetWindow());
            requestsVO.setBindTxnName(request.getTxnName());
            requestsVO.setBindRequestId(request.getRequestId());
            requestsVO.executeQuery();
            requestsVO.reset();
            while (requestsVO.hasNext()) {
                depRequestRow =
                        (IntgDeploymentRequestsVORowImpl)requestsVO.next();
                System.out.println("Found in the DB : " +
                                   request.getTargetWindow() + "=" +
                                   depRequestRow.getTargetWindow() + " " +
                                   request.getTxnName() + "=" +
                                   depRequestRow.getTxnName() + " " +
                                   request.getRequestId());

                logged = false;
                return logged;
            }
            if (depRequestRow == null) {
                System.out.println("Not Found in the DB : " +
                                   request.getTargetWindow() + " " +
                                   request.getTxnName());
                depRequestRow =
                        (IntgDeploymentRequestsVORowImpl)deployRequestVO.createRow();
                request.setRequestId(UniqueIdHelper.getNextId().longValue());
            }
            Calendar calendar = Calendar.getInstance();
            java.sql.Timestamp currentTimestamp =
                new java.sql.Timestamp(calendar.getTime().getTime());
            depRequestRow.setRequestId(request.getRequestId());
            depRequestRow.setCreationDate(currentTimestamp);
            depRequestRow.setDeploymentStatus("REQUESTED");
            depRequestRow.setEnvironmentId(request.getEnvironment().getEnvironmentId());
            depRequestRow.setProductCode(request.getProductCode());
            depRequestRow.setRequestorEmail(request.getRequestorEmail());
            depRequestRow.setTxnName(request.getTxnName());
            depRequestRow.setTargetWindow(request.getTargetWindow());
            depRequestRow.setParentRequestId(request.getParentRequestId());

            Set<String> depTypes = request.getDeployTypes();
            String depTypesString = depTypes.toString();
            depTypesString =
                    depTypesString.substring(1, depTypesString.length() - 1);
            depRequestRow.setDeployTypes(depTypesString);

            Set<String> soaComps = request.getSoaComposites();
            if (!soaComps.isEmpty()) {
                String soaCompString = soaComps.toString();
                soaCompString =
                        soaCompString.substring(1, soaCompString.length() - 1);
                depRequestRow.setSoaComposites(soaCompString);
            } else {
                depRequestRow.setSoaComposites(null);
            }
            deployRequestVO.insertRow(depRequestRow);
            this.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
            logged = false;
        }

        return logged;
    }
    
    public String getIntegratorsStringForProduct(String productCode) {
        String intgEmailsWithSpace = null;
        IntgProductsVOImpl productsVO = this.getIntgProductsVO();
        ViewCriteria criteria =
            productsVO.getViewCriteriaManager().getViewCriteria("ByProductCode");
        productsVO.applyViewCriteria(criteria);
        if (productCode != null) {
            productsVO.setbindProductCode(productCode);
        }

        productsVO.executeQuery();
        while (productsVO.hasNext()) {
            IntgProductsVORowImpl pRow =
                (IntgProductsVORowImpl)productsVO.next();
            String emailIDs = pRow.getIntegratorEmailIds();
            if (emailIDs != null) {
                intgEmailsWithSpace = emailIDs.replaceAll(",", " ");
            }
        }
        return intgEmailsWithSpace;
    }
    
    public Map<String, DeploymentRequest> getDeploymentRequests4NextCycle(Long envId,
                                                                          String status,
                                                                          String targetWindow, String pCode) {
        Map<String, DeploymentRequest> requests =
            new HashMap<String, DeploymentRequest>();
        IntgDeploymentRequestsVOImpl requestsVO =
            this.getIntgDeploymentRequestsVO();
        ViewCriteria criteria =
            requestsVO.getViewCriteriaManager().getViewCriteria("ForNextCycle");
        requestsVO.applyViewCriteria(criteria);
        requestsVO.setBindEnvironmentId(envId);
        requestsVO.setBindTargetWindow(targetWindow);
        requestsVO.setBindStatus(status);
        requestsVO.setBindProductCode(pCode);
        Calendar calendar = Calendar.getInstance();
        java.sql.Timestamp currentTimestamp =
            new java.sql.Timestamp(calendar.getTime().getTime());
        requestsVO.setBindCurrentDate(currentTimestamp);
        requestsVO.executeQuery();
        while (requestsVO.hasNext()) {
            IntgDeploymentRequestsVORowImpl reqRow =
                (IntgDeploymentRequestsVORowImpl)requestsVO.next();
            DeploymentRequest request = new DeploymentRequest();
            request.setDeploymentStatus(reqRow.getDeploymentStatus());
            request.setProductCode(reqRow.getProductCode());
            request.setRequestId(reqRow.getRequestId());
            request.setRequestorEmail(reqRow.getRequestorEmail());
            request.setTargetWindow(reqRow.getTargetWindow());
            request.setTxnName(reqRow.getTxnName());
            String txnType = reqRow.getDeployTypes();
            if (txnType != null) {
                String[] depTypes = txnType.split(",");
                for (int i = 0; i < depTypes.length; i++) {
                    request.getDeployTypes().add(depTypes[i].trim());
                }
            }
            String soaCompositesString = reqRow.getSoaComposites();
            if (soaCompositesString != null) {
                String[] soaComposites = soaCompositesString.split(",");
                for (int i = 0; i < soaComposites.length; i++) {
                    request.getSoaComposites().add(soaComposites[i]);
                }
            }
            Long environmentId = reqRow.getEnvironmentId();
            Environment env = getEnvironment(environmentId);
            request.setEnvironment(env);
            requests.put(request.getTxnName(), request);
        }
        return requests;
    }
    
    public boolean updateTxnFetchStatus(Long requestId, String targetWindow,
                                        String txnName, String status) {
        boolean updated = false;
        IntgDeploymentRequestsVOImpl requestsVO =
            this.getIntgDeploymentRequestsVO();
        ViewCriteria criteria =
            requestsVO.getViewCriteriaManager().getViewCriteria("ByDeploymentCriteria");
        requestsVO.applyViewCriteria(criteria);
        requestsVO.setBindTxnName(txnName);
        requestsVO.setBindTargetWindow(targetWindow);
        requestsVO.setBindRequestId(requestId);
        requestsVO.executeQuery();
        while (requestsVO.hasNext()) {
            IntgDeploymentRequestsVORowImpl reqRow =
                (IntgDeploymentRequestsVORowImpl)requestsVO.next();
            if (reqRow.getRequestId().longValue() == requestId.longValue()) {
                reqRow.setDeploymentStatus(status);
                reqRow.setTargetWindow(targetWindow);
            } else if ("FETCHED".equalsIgnoreCase(status)) {
                reqRow.setDeploymentStatus("OVERRIDDEN");
            }
        }
        getTransaction().commit();
        return updated;
    }


}


