package com.oracle.xmlns.ust.tools.bot.integrator.service;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;
// This source file is generated by Oracle tools.
// Contents may be subject to change.
// For reporting problems, use the following:
// Generated by Oracle JDeveloper 12c Development Build 12.2.1.1.0.2047
public class IntegratorServiceSoapHttpPortClient {
    public static void main(String[] args) {
        try {
            IntegratorService_Service integratorService_Service = new IntegratorService_Service();

            // Configure security feature
            SecurityPoliciesFeature securityFeatures =
                new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_client_policy" });
            IntegratorService integratorService =
                integratorService_Service.getIntegratorServiceSoapHttpPort(securityFeatures);

            // Add your code to call the desired methods.
            Map<String, Object> requestContext = ((BindingProvider) integratorService).getRequestContext();
            requestContext.put(BindingProvider.USERNAME_PROPERTY, "SCMOPERATIONS");
            requestContext.put(BindingProvider.PASSWORD_PROPERTY, "Welcome1");


            String output = integratorService.process("R13_OAL_ST0A_UNLOCKED ADF utulasi_inttestc");
            System.out.println(output);
            // Add your code to call the desired methods.

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
