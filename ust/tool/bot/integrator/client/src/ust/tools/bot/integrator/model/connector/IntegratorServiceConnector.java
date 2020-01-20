package ust.tools.bot.integrator.model.connector;

import com.oracle.xmlns.ust.tools.bot.integrator.service.IntegratorService;
import com.oracle.xmlns.ust.tools.bot.integrator.service.IntegratorService_Service;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import oracle.jbo.client.Configuration;

import ust.tools.bot.integrator.model.applicationModule.IntegratorServiceAMImpl;
import ust.tools.bot.integrator.model.lang.Messages;
import ust.tools.bot.integrator.model.util.Cache;
import ust.tools.bot.integrator.model.util.DeploymentRequest;

import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class IntegratorServiceConnector {


    public static synchronized String processRequest(DeploymentRequest request) {
        String outMessage = Messages.PROCESSING_STATUS_SUCCESS;
        try {
            IntegratorService_Service integratorService_Service = new IntegratorService_Service();

            // Configure security feature
            SecurityPoliciesFeature securityFeatures =
                new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_client_policy" });
            IntegratorService integratorService =
                integratorService_Service.getIntegratorServiceSoapHttpPort(securityFeatures);

            Map<String, Object> requestContext = ((BindingProvider) integratorService).getRequestContext();
            requestContext.put(BindingProvider.USERNAME_PROPERTY, "SCMOPERATIONS");
            requestContext.put(BindingProvider.PASSWORD_PROPERTY, "Welcome1");

            String output = integratorService.process(request.getRequestId().toString());
            System.out.println(output);

            if (output != null) {
                if (output.indexOf("conflicts are outstanding )") > -1) {
                    outMessage = Messages.PROCESSING_STATUS_CONFLICT;
                } else if (output.indexOf(request.getTxnName() + " does not exist in backend") > -1) {
                    outMessage = Messages.PROCESSING_STATUS_NOTXN;
                }
            }
            Cache.refreshDeployRequests();
        } catch (Exception e) {
            outMessage = Messages.PROCESSING_STATUS_ERROR;
            e.printStackTrace();
        }
        return outMessage;

    }


    public static synchronized String processRequestLocal(DeploymentRequest request) {
        String outMessage = Messages.PROCESSING_STATUS_SUCCESS;
        IntegratorServiceAMImpl intgAM = null;
        try {
            intgAM =
                (IntegratorServiceAMImpl) Configuration.createRootApplicationModule("ust.tools.bot.integrator.model.applicationModule.IntegratorServiceAM",
                                                                                    "IntegratorServiceAMLocal");
            String output = intgAM.process(request.getRequestId().toString());
            intgAM.getDBTransaction().commit();
            System.out.println(output);
            if (output != null) {
                if (output.indexOf("conflicts are outstanding )") > -1) {
                    outMessage = Messages.PROCESSING_STATUS_CONFLICT;
                } else if (output.indexOf(request.getTxnName() + " does not exist in backend") > -1) {
                    outMessage = Messages.PROCESSING_STATUS_NOTXN;
                } 
            }
            Cache.refreshDeployRequests();
            if (Messages.PROCESSING_STATUS_SUCCESS.equals(outMessage)) {
                if("Y".equals(request.getEnvironment().getBuildFlag())){
                    
                    Cache.scheduleTimedTask(request.getTargetWindow(), request.getEnvironment());  
                }
                else
                 System.out.println("Build is disabled for this environemnt ");
            }
        } catch (Exception e) {
            outMessage = Messages.PROCESSING_STATUS_ERROR;
            e.printStackTrace();
        }
        return outMessage;
    }
}
