package com.thisal.supply_chain_ejb.ejb.shipment;

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
    public Vendor createVendor(String name, String email) {
        return Vendor.builder()
                ._id(UUID.randomUUID())
                .name(name)
                .email(email)
                .build();
    }

    @Override
    public List<Vendor> getAllVendors() {
        return entityManager.createQuery("SELECT v FROM Vendor v", Vendor.class)
                .getResultList();
    }

}