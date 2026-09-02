package com.thisal.supply_chain_ejb.ejb.vendor;

import com.thisal.supply_chain_core.dto.VendorDTO;
import com.thisal.supply_chain_core.entity.Vendor;
import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.enums.VendorStatus;
import com.thisal.supply_chain_core.exception.VendorAlreadyExistsException;
import com.thisal.supply_chain_core.exception.VendorNotFoundException;
import com.thisal.supply_chain_core.mapper.Mapper;
import com.thisal.supply_chain_core.model.ResponseModel;
import jakarta.enterprise.event.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Mapper mapper;

    @Mock
    private Event<String> logEvent;

    @Mock
    private TypedQuery<Vendor> vendorQuery;

    @InjectMocks
    private VendorBean vendorBean;

    @BeforeEach
    void setUp() {
        lenient().when(entityManager.createNamedQuery("Vendor.findByEmail", Vendor.class)).thenReturn(vendorQuery);
        lenient().when(vendorQuery.setParameter(eq("email"), anyString())).thenReturn(vendorQuery);
    }

    @Test
    void createVendorShouldPersistNewVendor() {
        when(vendorQuery.getResultList()).thenReturn(Collections.emptyList());
        when(mapper.toVendorDTO(any(Vendor.class))).thenReturn(VendorDTO.builder()
                .name("Test")
                .email("test@example.com")
                .vendorStatus(VendorStatus.PENDING)
                .build());

        ResponseModel response = vendorBean.createVendor("Test", "test@example.com");

        assertEquals(ResponseStatus.OK, response.getStatus());
        assertEquals("Vendor created Successfully!", response.getMessage());
        verify(entityManager).persist(any(Vendor.class));
    }

    @Test
    void createVendorShouldThrowWhenVendorAlreadyExists() {
        Vendor existingVendor = Vendor.builder().name("Test").email("test@example.com").vendorStatus(VendorStatus.PENDING).build();
        when(vendorQuery.getResultList()).thenReturn(List.of(existingVendor));

        assertThrows(VendorAlreadyExistsException.class,
                () -> vendorBean.createVendor("Test", "test@example.com"));
    }

    @Test
    void approveVendorShouldActivateVendor() {
        Vendor existingVendor = Vendor.builder().name("Test").email("test@example.com").vendorStatus(VendorStatus.PENDING).build();
        when(vendorQuery.getResultList()).thenReturn(List.of(existingVendor));
        when(mapper.toVendorDTO(any(Vendor.class))).thenReturn(VendorDTO.builder()
                .name("Test")
                .email("test@example.com")
                .vendorStatus(VendorStatus.ACTIVE)
                .build());

        ResponseModel response = vendorBean.approveVendor("test@example.com");

        assertEquals(ResponseStatus.OK, response.getStatus());
        assertEquals(VendorStatus.ACTIVE, existingVendor.getVendorStatus());
    }

    @Test
    void approveVendorShouldThrowWhenVendorNotFound() {
        when(vendorQuery.getResultList()).thenReturn(Collections.emptyList());

        assertThrows(VendorNotFoundException.class,
                () -> vendorBean.approveVendor("missing@example.com"));
    }

}