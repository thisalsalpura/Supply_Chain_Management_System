package com.thisal.supply_chain_core.enums;

import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {
    OK(Response.Status.OK),
    NOT_FOUND(Response.Status.NOT_FOUND),
    BAD_REQUEST(Response.Status.BAD_REQUEST),
    UNAUTHORIZED(Response.Status.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR(Response.Status.INTERNAL_SERVER_ERROR);

    private final Response.Status httpStatus;
}