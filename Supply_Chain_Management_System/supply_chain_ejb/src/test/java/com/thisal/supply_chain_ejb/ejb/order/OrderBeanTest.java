package com.thisal.supply_chain_ejb.ejb.order;

import com.thisal.supply_chain_core.dto.OrderRequestDTO;
import com.thisal.supply_chain_core.entity.InventoryItem;
import com.thisal.supply_chain_core.entity.Order;
import com.thisal.supply_chain_core.entity.Vendor;
import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.exception.InsufficientStockException;
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
class OrderBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Mapper mapper;

    @Mock
    private Event<String> logEvent;

    @Mock
    private TypedQuery<Vendor> vendorQuery;

    @Mock
    private TypedQuery<InventoryItem> inventoryQuery;

    @InjectMocks
    private OrderBean orderBean;

    @BeforeEach
    void setUp() {
        lenient().when(entityManager.createNamedQuery("Vendor.findByEmail", Vendor.class)).thenReturn(vendorQuery);
        lenient().when(vendorQuery.setParameter(eq("email"), anyString())).thenReturn(vendorQuery);
        lenient().when(entityManager.createNamedQuery("InventoryItem.findBySku", InventoryItem.class)).thenReturn(inventoryQuery);
        lenient().when(inventoryQuery.setParameter(eq("sku"), anyString())).thenReturn(inventoryQuery);
    }

    @Test
    void placeOrderShouldReduceInventoryAndPersistOrder() {
        Vendor vendor = Vendor.builder().name("Test").email("test@example.com").build();
        InventoryItem item = InventoryItem.builder().sku("SKU-01").name("Widget").qtyOnHand(20).reorderThreshold(5).build();
        when(vendorQuery.getResultList()).thenReturn(List.of(vendor));
        when(inventoryQuery.getResultList()).thenReturn(List.of(item));
        when(mapper.toOrderDTO(any(Order.class))).thenReturn(new com.thisal.supply_chain_core.dto.OrderDTO());

        ResponseModel response = orderBean.placeOrder("test@example.com", List.of(
                OrderRequestDTO.builder().sku("SKU-01").qty(5).build()));

        assertEquals(ResponseStatus.OK, response.getStatus());
        assertEquals(15, item.getQtyOnHand());
        verify(entityManager).persist(any(Order.class));
    }

    @Test
    void placeOrderShouldThrowWhenVendorDoesNotExist() {
        when(vendorQuery.getResultList()).thenReturn(Collections.emptyList());

        assertThrows(VendorNotFoundException.class,
                () -> orderBean.placeOrder("missing@example.com", List.of(OrderRequestDTO.builder().sku("SKU-01").qty(1).build())));
    }

    @Test
    void placeOrderShouldThrowWhenInventoryIsInsufficient() {
        Vendor vendor = Vendor.builder().name("Test").email("test@example.com").build();
        InventoryItem item = InventoryItem.builder().sku("SKU-01").name("Widget").qtyOnHand(2).reorderThreshold(1).build();
        when(vendorQuery.getResultList()).thenReturn(List.of(vendor));
        when(inventoryQuery.getResultList()).thenReturn(List.of(item));

        assertThrows(InsufficientStockException.class,
                () -> orderBean.placeOrder("test@example.com", List.of(OrderRequestDTO.builder().sku("SKU-01").qty(3).build())));
    }

}