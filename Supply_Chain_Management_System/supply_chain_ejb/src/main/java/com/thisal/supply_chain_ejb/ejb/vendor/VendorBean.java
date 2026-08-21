package com.thisal.supply_chain_ejb.ejb.vendor;

import com.thisal.supply_chain_core.annotation.Console;
import com.thisal.supply_chain_core.dto.VendorDTO;
import com.thisal.supply_chain_core.entity.Vendor;
import com.thisal.supply_chain_core.exception.VendorAlreadyExistsException;
import com.thisal.supply_chain_core.mapper.Mapper;
import com.thisal.supply_chain_core.service.VendorService;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import java.util.List;
import java.util.UUID;

@Stateless
public class VendorBean implements VendorService {

    @PersistenceContext(unitName = "supply_chainPU")
    private EntityManager entityManager;

    @Inject
    private Mapper mapper;

    @Inject
    @Console
    private Event<String> logEvent;

    @Override
    public VendorDTO createVendor(String name, String email) {
        Vendor existingVendor = entityManager.createNamedQuery("Vendor.findByEmail", Vendor.class)
                .setParameter("email", email)
                .getSingleResult();
        if (existingVendor != null) {
            logEvent.fire("Vendor with " + email + " is already Registered.");
            throw new VendorAlreadyExistsException("Vendor with " + email + " is already Registered.");
        } else {
            Vendor vendor = Vendor.builder()
                    ._id(UUID.randomUUID())
                    .name(name)
                    .email(email.trim().toLowerCase())
                    .build();
            try {
                entityManager.persist(vendor);
                entityManager.flush();
                return mapper.toVendorDTO(vendor);
            } catch (PersistenceException e) {
                logEvent.fire("Something went Wrong! Please try again Later.");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<VendorDTO> getAllVendors() {
        List<Vendor> vendorList = entityManager.createNamedQuery("Vendor.findAll", Vendor.class)
                .getResultList();
        return mapper.toVendorDTOList(vendorList);
    }

}