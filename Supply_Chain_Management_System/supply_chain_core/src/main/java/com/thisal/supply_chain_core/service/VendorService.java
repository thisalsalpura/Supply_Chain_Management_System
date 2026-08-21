package com.thisal.supply_chain_core.service;

import com.thisal.supply_chain_core.dto.VendorDTO;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface VendorService {

    VendorDTO createVendor(String name, String email);

    List<VendorDTO> getAllVendors();

}