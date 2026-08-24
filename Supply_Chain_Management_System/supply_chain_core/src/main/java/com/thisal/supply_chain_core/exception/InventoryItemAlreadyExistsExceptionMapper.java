package com.thisal.supply_chain_core.exception;

import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.model.ResponseModel;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InventoryItemAlreadyExistsExceptionMapper implements ExceptionMapper<InventoryItemAlreadyExistsException> {

    @Override
    public Response toResponse(InventoryItemAlreadyExistsException exception) {
        ResponseModel responseModel = ResponseModel.builder()
                .status(ResponseStatus.BAD_REQUEST)
                .message(exception.getMessage())
                .build();
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

}