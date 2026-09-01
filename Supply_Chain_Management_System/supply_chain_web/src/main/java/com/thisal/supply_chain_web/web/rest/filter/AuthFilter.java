package com.thisal.supply_chain_web.web.rest.filter;

import com.thisal.supply_chain_core.util.security.JWTUtil;
import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();
        Class<?> resourceClass = resourceInfo.getResourceClass();

        RolesAllowed rolesOnMethod = method.getAnnotation(RolesAllowed.class);
        RolesAllowed rolesOnClass = resourceClass.getAnnotation(RolesAllowed.class);

        RolesAllowed effective = rolesOnMethod != null ? rolesOnMethod : rolesOnClass;

        if (effective != null) {
            String authHeader = requestContext.getHeaderString("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Authorization Required!").build());
                return;
            }
            String token = authHeader.substring(7);
            if (!JWTUtil.isValidToken(token)) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid Token!").build());
                return;
            }
            List<String> roles = JWTUtil.getRolesFromToken(token);
            Set<String> roleSet = new HashSet<>(roles);
            boolean allowed = false;
            for (String r : effective.value()) {
                if (roleSet.contains(r)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Forbidden!").build());
            }
        }
    }

}