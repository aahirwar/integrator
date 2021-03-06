
package com.oracle.xmlns.ust.tools.bot.integrator.service;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.11-b150616.1732
 * Generated source version: 2.2
 *
 */
@WebServiceClient(name = "IntegratorService",
                  targetNamespace = "http://xmlns.oracle.com/ust/tools/bot/integrator/service/",
                  wsdlLocation =
                  "http://slc06ukb.us.oracle.com:7011/Integrator/IntegratorService?WSDL#%7Bhttp%3A%2F%2Fxmlns.oracle.com%2Fust%2Ftools%2Fbot%2Fintegrator%2Fservice%2F%7DIntegratorService")
public class IntegratorService_Service extends Service {

    private final static URL INTEGRATORSERVICE_WSDL_LOCATION;
    private final static WebServiceException INTEGRATORSERVICE_EXCEPTION;
    private final static QName INTEGRATORSERVICE_QNAME =
        new QName("http://xmlns.oracle.com/ust/tools/bot/integrator/service/", "IntegratorService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url =
                new URL("http://slc06ukb.us.oracle.com:7011/Integrator/IntegratorService?WSDL#%7Bhttp%3A%2F%2Fxmlns.oracle.com%2Fust%2Ftools%2Fbot%2Fintegrator%2Fservice%2F%7DIntegratorService");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        INTEGRATORSERVICE_WSDL_LOCATION = url;
        INTEGRATORSERVICE_EXCEPTION = e;
    }

    public IntegratorService_Service() {
        super(__getWsdlLocation(), INTEGRATORSERVICE_QNAME);
    }

    public IntegratorService_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), INTEGRATORSERVICE_QNAME, features);
    }

    public IntegratorService_Service(URL wsdlLocation) {
        super(wsdlLocation, INTEGRATORSERVICE_QNAME);
    }

    public IntegratorService_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, INTEGRATORSERVICE_QNAME, features);
    }

    public IntegratorService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public IntegratorService_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns IntegratorService
     */
    @WebEndpoint(name = "IntegratorServiceSoapHttpPort")
    public IntegratorService getIntegratorServiceSoapHttpPort() {
        return super.getPort(new QName("http://xmlns.oracle.com/ust/tools/bot/integrator/service/",
                                       "IntegratorServiceSoapHttpPort"), IntegratorService.class);
    }

    /**
     *
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IntegratorService
     */
    @WebEndpoint(name = "IntegratorServiceSoapHttpPort")
    public IntegratorService getIntegratorServiceSoapHttpPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://xmlns.oracle.com/ust/tools/bot/integrator/service/",
                                       "IntegratorServiceSoapHttpPort"), IntegratorService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (INTEGRATORSERVICE_EXCEPTION != null) {
            throw INTEGRATORSERVICE_EXCEPTION;
        }
        return INTEGRATORSERVICE_WSDL_LOCATION;
    }

}
