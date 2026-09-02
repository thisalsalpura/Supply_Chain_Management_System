package com.thisal.supply_chain_web.web.rest.resource;

import com.thisal.supply_chain_core.annotation.Console;
import com.thisal.supply_chain_core.exception.InvalidCredentialsException;
import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.record.LoginRequestRecord;
import com.thisal.supply_chain_core.record.RegisterRequestRecord;
import com.thisal.supply_chain_core.service.UserService;
import com.thisal.supply_chain_core.util.security.JWTUtil;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/auth")
@RequestScoped
public class AuthREST {

    @EJB
    private UserService userService;

    @Inject
    @Console
    private Event<String> logEvent;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequestRecord registerRequestRecord) {
        ResponseModel responseModel = userService.register(registerRequestRecord.username(), registerRequestRecord.password(), registerRequestRecord.roles());
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequestRecord loginRequestRecord) {
        return userService.login(loginRequestRecord.username(), loginRequestRecord.password())
                .map(userPrincipalRecord -> {
                    String token = JWTUtil.generateToken(userPrincipalRecord.username(), userPrincipalRecord.roles());
                    return Response.ok(Map.of(
                            "accessToken", token,
                            "username", userPrincipalRecord.username(),
                            "roles", userPrincipalRecord.roles()
                    )).build();
                }).orElseThrow(() -> {
                    logEvent.fire("Invalid Username or Password!");
                    return new InvalidCredentialsException("Invalid Username or Password!");
                });
    }

}