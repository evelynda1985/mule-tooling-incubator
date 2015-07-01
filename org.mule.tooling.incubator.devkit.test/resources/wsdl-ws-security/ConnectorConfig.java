package org.mule.modules.cloud.config;

import java.util.ArrayList;
import java.util.List;

import org.mule.api.annotations.ws.WsdlSecurity;
import org.mule.devkit.api.ws.authentication.WsdlSecurityStrategy;
import org.mule.devkit.api.ws.authentication.WsdlUsernameToken;
import org.mule.module.ws.security.PasswordType;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.components.WsdlProvider;
import org.mule.api.annotations.ws.WsdlServiceEndpoint;
import org.mule.api.annotations.ws.WsdlServiceRetriever;
import org.mule.devkit.api.ws.definition.DefaultServiceDefinition;
import org.mule.devkit.api.ws.definition.ServiceDefinition;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;

@WsdlProvider(friendlyName = "Config")
public class ConnectorConfig {

    @Configurable
    @Placement(order = 1)
    private String username;

    @Configurable
    @Placement(order = 2)
    @Password
    @Optional
    private String password;

    @Configurable
    @Default("https://login.salesforce.com/services/Soap/c/22.0")
    @Placement(order = 3)
    private String endpoint;

    @WsdlServiceRetriever
    public ServiceDefinition getServiceDefinition() {
           return new DefaultServiceDefinition(
                "SforceService_Soap",
                "Salesforce API",
                "wsdl/enterprise.wsdl",
                "SforceService",
                "Soap");
    }

    @WsdlServiceEndpoint
    public String getServiceAddress(ServiceDefinition definition) {
         return endpoint;
    }

    @WsdlSecurity
    public List<WsdlSecurityStrategy> getWsdlSecurityResolver(ServiceDefinition definition) {
        List<WsdlSecurityStrategy> result = new ArrayList<WsdlSecurityStrategy>();
        result.add(new WsdlUsernameToken(getUsername(),  getPassword(), PasswordType.TEXT, true, true));
        return result;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}