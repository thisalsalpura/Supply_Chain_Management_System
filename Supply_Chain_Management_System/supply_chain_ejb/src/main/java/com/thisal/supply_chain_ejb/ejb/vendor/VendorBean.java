package com.thisal.supply_chain_ejb.ejb.vendor;

import com.thisal.supply_chain_core.annotation.Audited;
import com.thisal.supply_chain_core.annotation.Console;
import com.thisal.supply_chain_core.annotation.Validated;
import com.thisal.supply_chain_core.entity.Vendor;
import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.enums.VendorStatus;
import com.thisal.supply_chain_core.exception.VendorAlreadyExistsException;
import com.thisal.supply_chain_core.exception.VendorNotFoundException;
import com.thisal.supply_chain_core.mapper.Mapper;
import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.service.VendorService;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import java.util.List;

@Stateless
@Audited
@Validated
@DeclareRoles({"ADMIN", "WAREHOUSE_MANAGER", "VENDOR", "USER"})
public class VendorBean implements VendorService {

    @PersistenceContext(unitName = "supply_chainPU")
    private EntityManager entityManager;

    @Inject
    private Mapper mapper;

    @Inject
    @Console
    private Event<String> logEvent;

    @Override
    @PermitAll
    public ResponseModel createVendor(String name, String email) {
        Vendor existingVendor = entityManager.createNamedQuery("Vendor.findByEmail", Vendor.class)
                .setParameter("email", email.trim().toLowerCase())
                .getResultList().stream().findFirst().orElse(null);
        if (existingVendor != null) {
            logEvent.fire("Vendor with " + email + " is already Registered!");
            throw new VendorAlreadyExistsException("Vendor with " + email + " is already Registered!");
        } else {
            Vendor vendor = Vendor.builder()
                    .name(name)
                    .email(email.trim().toLowerCase())
                    .build();
            try {
                entityManager.persist(vendor);
                entityManager.flush();
                logEvent.fire("Vendor created Successfully!");
                return ResponseModel.builder()
                        .status(ResponseStatus.OK)
                        .payload(mapper.toVendorDTO(vendor))
                        .message("Vendor created Successfully!")
                        .build();
            } catch (PersistenceException e) {
                logEvent.fire(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @PermitAll
    public ResponseModel approveVendor(String email) {
        Vendor vendor = entityManager.createNamedQuery("Vendor.findByEmail", Vendor.class)
                .setParameter("email", email.trim().toLowerCase())
                .getResultList().stream().findFirst().orElse(null);
        if (vendor != null) {
            vendor.setVendorStatus(VendorStatus.ACTIVE);
            logEvent.fire("Vendor approved Successfully!");
            return ResponseModel.builder()
                    .status(ResponseStatus.OK)
                    .payload(mapper.toVendorDTO(vendor))
                    .message("Vendor approved Successfully!")
                    .build();
        } else {
            logEvent.fire("Vendor with " + email + " is not Found!");
            throw new VendorNotFoundException("Vendor with " + email + " is not Found!");
        }
    }

    @Override
    @PermitAll
    public ResponseModel getAllVendors() {
        List<Vendor> vendorList = entityManager.createNamedQuery("Vendor.findAll", Vendor.class)
                .getResultList();
        return ResponseModel.builder()
                .status(ResponseStatus.OK)
                .payload(mapper.toVendorDTOList(vendorList))
                .build();
    }

}