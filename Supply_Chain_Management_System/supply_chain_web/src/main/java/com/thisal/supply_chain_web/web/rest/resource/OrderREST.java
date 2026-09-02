package com.thisal.supply_chain_web.web.rest.resource;

import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.record.OrderRequestRecord;
import com.thisal.supply_chain_core.service.OrderService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/order")
@RequestScoped
public class OrderREST {

    @EJB
    private OrderService orderService;

    @POST
    @Path("/{vendorEmail}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"WAREHOUSE_MANAGER", "VENDOR"})
    public Response placeOrder(@PathParam("vendorEmail") String vendorEmail, OrderRequestRecord orderRequestRecord) {
        ResponseModel responseModel = orderService.placeOrder(vendorEmail, orderRequestRecord.orderRequestDTOs());
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrders() {
        ResponseModel responseModel = orderService.getAllOrders();
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

}