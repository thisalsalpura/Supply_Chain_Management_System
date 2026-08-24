package com.thisal.supply_chain_core.exception;

import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.model.ResponseModel;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class VendorNotFoundExceptionMapper implements ExceptionMapper<VendorNotFoundException> {

    @Override
    public Response toResponse(VendorNotFoundException exception) {
        ResponseModel responseModel = ResponseModel.builder()
                .status(ResponseStatus.NOT_FOUND)
                .payload(null)
                .message(exception.getMessage())
                .build();
        return Response.status(responseModel.getStatus().getHttpStatus())
                .entity(responseModel)
                .build();
    }

}