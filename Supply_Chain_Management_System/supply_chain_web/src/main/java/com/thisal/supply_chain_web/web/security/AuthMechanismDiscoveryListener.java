package com.thisal.supply_chain_web.web.security;

import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.ServiceLoader;

@WebListener
public class AuthMechanismDiscoveryListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        System.out.println("[AuthDiscovery] contextInitialized: classloader=" + cl);
        try {
            Enumeration<URL> resources = cl.getResources("META-INF/services/jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism");
            while (resources.hasMoreElements()) {
                URL u = resources.nextElement();
                System.out.println("[AuthDiscovery] found service resource: " + u);
            }
        } catch (IOException e) {
            System.out.println("[AuthDiscovery] error listing service resources: " + e.getMessage());
        }

        ServiceLoader<HttpAuthenticationMechanism> loader = ServiceLoader.load(HttpAuthenticationMechanism.class, cl);
        int count = 0;
        for (HttpAuthenticationMechanism mech : loader) {
            count++;
            System.out.println("[AuthDiscovery] ServiceLoader found: " + mech.getClass().getName());
        }
        System.out.println("[AuthDiscovery] total mechanisms found: " + count);
    }

}