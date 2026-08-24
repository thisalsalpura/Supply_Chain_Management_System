package com.thisal.supply_chain_core.model;

import com.thisal.supply_chain_core.enums.ResponseStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseModel {

    ResponseStatus status;
    Object payload;
    String message;

}