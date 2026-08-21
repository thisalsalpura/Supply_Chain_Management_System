package com.thisal.supply_chain_core.dto;

import com.thisal.supply_chain_core.enums.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorDTO {

    private UUID _id;
    private String name;
    private String email;
    private VendorStatus vendorStatus;
    private LocalDateTime createdAt;

}