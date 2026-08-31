package com.thisal.supply_chain_web.web.rest.resource;

import com.thisal.supply_chain_core.dto.VendorDTO;
import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.service.VendorService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/vendor")
@RequestScoped
public class VendorREST {

    @EJB
    private VendorService vendorService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER"})
    public Response createVendor(VendorDTO vendorDTO) {
        ResponseModel responseModel = vendorService.createVendor(vendorDTO.getName(), vendorDTO.getEmail());
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "WAREHOUSE_MANAGER"})
    public Response approveVendor(VendorDTO vendorDTO) {
        ResponseModel responseModel = vendorService.approveVendor(vendorDTO.getEmail());
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVendors() {
        ResponseModel responseModel = vendorService.getAllVendors();
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

}