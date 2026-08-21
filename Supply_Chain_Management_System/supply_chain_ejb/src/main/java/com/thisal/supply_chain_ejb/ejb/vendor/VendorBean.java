package com.thisal.supply_chain_ejb.ejb.vendor;

import com.thisal.supply_chain_core.dto.VendorDTO;
import com.thisal.supply_chain_core.entity.Vendor;
import com.thisal.supply_chain_core.service.VendorService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.UUID;

@Stateless
public class VendorBean implements VendorService {

    @PersistenceContext(unitName = "supply_chainPU")
    private EntityManager entityManager;

    @Override
    public VendorDTO createVendor(String name, String email) {
        Vendor vendor = Vendor.builder()
                ._id(UUID.randomUUID())
                .name(name)
                .email(email)
                .build();
        entityManager.persist(vendor);
        return null;
    }

    @Override
    public List<VendorDTO> getAllVendors() {
        List<Vendor> vendorList = entityManager.createQuery("SELECT v FROM Vendor v", Vendor.class)
                .getResultList();
        return null;
    }

}