package com.thisal.supply_chain_web.web.rest.resource;

import com.thisal.supply_chain_core.dto.VendorDTO;
import com.thisal.supply_chain_core.service.VendorService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/vendor")
@RequestScoped
public class VendorREST {

    @EJB
    private VendorService vendorService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VendorDTO createVendor(VendorDTO vendorDTO) {
        return vendorService.createVendor(vendorDTO.getName(), vendorDTO.getEmail());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<VendorDTO> getAllVendors() {
        return vendorService.getAllVendors();
    }

}