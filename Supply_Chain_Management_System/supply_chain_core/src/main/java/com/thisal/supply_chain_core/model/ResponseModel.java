package com.thisal.supply_chain_core.model;

import com.thisal.supply_chain_core.enums.ResponseStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseModel {

    private ResponseStatus status;
    private Object payload;
    private String message;

}