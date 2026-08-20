package com.thisal.supply_chain_core.service;

import com.thisal.supply_chain_core.entity.Vendor;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface VendorService {

    Vendor createVendor(String name, String email);

    List<Vendor> getAllVendors();

}