package com.thisal.supply_chain_web.web.rest.resource;

import com.thisal.supply_chain_core.dto.InventoryItemDTO;
import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.service.InventoryItemService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/inventory")
@RequestScoped
public class InventoryItemREST {

    @EJB
    private InventoryItemService inventoryItemService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("WAREHOUSE_MANAGER")
    public Response createInventoryItem(InventoryItemDTO inventoryItemDTO) {
        ResponseModel responseModel = inventoryItemService.createInventoryItem(inventoryItemDTO.getSku(), inventoryItemDTO.getName(), inventoryItemDTO.getQtyOnHand(), inventoryItemDTO.getReorderThreshold());
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

    @PATCH
    @Path("/stock/{sku}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("WAREHOUSE_MANAGER")
    public Response updateStock(@PathParam("sku") String sku, InventoryItemDTO inventoryItemDTO) {
        ResponseModel responseModel = inventoryItemService.updateStock(sku, inventoryItemDTO.getQtyOnHand());
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllInventoryItems() {
        ResponseModel responseModel = inventoryItemService.getAllInventoryItems();
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

    @GET
    @Path("/low_stock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLowStockInventoryItems() {
        ResponseModel responseModel = inventoryItemService.getLowStockInventoryItems();
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

}