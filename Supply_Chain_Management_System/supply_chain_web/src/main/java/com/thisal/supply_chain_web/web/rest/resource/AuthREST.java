package com.thisal.supply_chain_web.web.rest.resource;

import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.record.RegisterRequestRecord;
import com.thisal.supply_chain_core.service.UserService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@RequestScoped
public class AuthREST {

    @EJB
    private UserService userService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequestRecord registerRequestRecord) {
        ResponseModel responseModel = userService.register(registerRequestRecord.username(), registerRequestRecord.password(), registerRequestRecord.roleNames());
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

}