package com.thisal.supply_chain_core.service;

import com.thisal.supply_chain_core.model.ResponseModel;
import jakarta.ejb.Local;

@Local
public interface VendorService {

    ResponseModel createVendor(String name, String email);

    ResponseModel approveVendor(String email);

    ResponseModel getAllVendors();

}