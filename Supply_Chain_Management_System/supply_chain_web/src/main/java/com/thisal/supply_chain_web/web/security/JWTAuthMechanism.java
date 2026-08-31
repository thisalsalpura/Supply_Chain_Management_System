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

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("DEBUG: authHeader=" + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("DEBUG: token=" + token);
            if (JWTUtil.isValidToken(token)) {
                String username = JWTUtil.getUsernameFromToken(token);
                List<String> roleList = JWTUtil.getRolesFromToken(token);
                System.out.println("DEBUG: username=" + username + " roles=" + roleList);
                HashSet<String> roles = new HashSet<>(roleList);
                return httpMessageContext.notifyContainerAboutLogin(username, roles);
            }
            if (httpMessageContext.isProtected()) {
                return httpMessageContext.responseUnauthorized();
            }
        }
        return httpMessageContext.doNothing();
    }

}