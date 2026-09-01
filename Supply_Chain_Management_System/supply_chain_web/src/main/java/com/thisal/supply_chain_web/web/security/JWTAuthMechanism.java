package com.thisal.supply_chain_web.web.security;

import com.thisal.supply_chain_core.util.security.JWTUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class JWTAuthMechanism implements HttpAuthenticationMechanism {

    public JWTAuthMechanism() {
        System.out.println("JWTAuthMechanism instantiated");
    }

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        System.out.println("JWTAuthMechanism.validateRequest called for path=" + request.getRequestURI());
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (JWTUtil.isValidToken(token)) {
                String username = JWTUtil.getUsernameFromToken(token);
                List<String> roleList = JWTUtil.getRolesFromToken(token);
                HashSet<String> roles = new HashSet<>(roleList);
                return httpMessageContext.notifyContainerAboutLogin(username, roles);
            } else {
                if (httpMessageContext.isProtected()) {
                    return httpMessageContext.responseUnauthorized();
                }
                return httpMessageContext.doNothing();
            }
        } else {
            if (httpMessageContext.isProtected()) {
                return httpMessageContext.responseUnauthorized();
            }
            return httpMessageContext.doNothing();
        }
    }

}