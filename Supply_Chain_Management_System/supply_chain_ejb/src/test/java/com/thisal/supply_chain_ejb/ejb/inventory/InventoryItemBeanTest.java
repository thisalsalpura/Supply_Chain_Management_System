package com.thisal.supply_chain_ejb.ejb.inventory;

import com.thisal.supply_chain_core.dto.InventoryItemDTO;
import com.thisal.supply_chain_core.entity.InventoryItem;
import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.exception.InventoryItemAlreadyExistsException;
import com.thisal.supply_chain_core.exception.InventoryItemNotFoundException;
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
class InventoryItemBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Mapper mapper;

    @Mock
    private Event<String> logEvent;

    @Mock
    private TypedQuery<InventoryItem> inventoryQuery;

    @InjectMocks
    private InventoryItemBean inventoryItemBean;

    @BeforeEach
    void setUp() {
        lenient().when(entityManager.createNamedQuery("InventoryItem.findBySku", InventoryItem.class)).thenReturn(inventoryQuery);
        lenient().when(inventoryQuery.setParameter(eq("sku"), anyString())).thenReturn(inventoryQuery);
    }

    @Test
    void createInventoryItemShouldPersistNewItem() {
        when(inventoryQuery.getResultList()).thenReturn(Collections.emptyList());
        when(mapper.toInventoryItemDTO(any(InventoryItem.class))).thenReturn(InventoryItemDTO.builder()
                .sku("SKU-100")
                .name("Widget")
                .qtyOnHand(25)
                .build());

        ResponseModel response = inventoryItemBean.createInventoryItem("SKU-100", "Widget", 25, 10);

        assertEquals(ResponseStatus.OK, response.getStatus());
        assertEquals("Inventory Item created Successfully!", response.getMessage());
        verify(entityManager).persist(any(InventoryItem.class));
        verify(logEvent).fire("Inventory Item created Successfully!");
    }

    @Test
    void createInventoryItemShouldRejectDuplicateSku() {
        InventoryItem item = InventoryItem.builder().sku("SKU-100").name("Widget").qtyOnHand(25).reorderThreshold(10).build();
        when(inventoryQuery.getResultList()).thenReturn(List.of(item));

        assertThrows(InventoryItemAlreadyExistsException.class,
                () -> inventoryItemBean.createInventoryItem("SKU-100", "Widget", 5, 10));
    }

    @Test
    void updateStockShouldIncreaseQuantityForExistingItem() {
        InventoryItem item = InventoryItem.builder().sku("SKU-100").name("Widget").qtyOnHand(20).reorderThreshold(5).build();
        when(inventoryQuery.getResultList()).thenReturn(List.of(item));
        when(mapper.toInventoryItemDTO(any(InventoryItem.class))).thenReturn(InventoryItemDTO.builder()
                .sku("SKU-100")
                .name("Widget")
                .qtyOnHand(30)
                .reorderThreshold(5)
                .build());

        ResponseModel response = inventoryItemBean.updateStock("SKU-100", 10);

        assertEquals(ResponseStatus.OK, response.getStatus());
        assertEquals(30, item.getQtyOnHand());
    }

    @Test
    void updateStockShouldThrowWhenItemDoesNotExist() {
        when(inventoryQuery.getResultList()).thenReturn(Collections.emptyList());

        assertThrows(InventoryItemNotFoundException.class,
                () -> inventoryItemBean.updateStock("MISSING-SKU", 10));
    }

}