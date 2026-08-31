package com.thisal.supply_chain_ejb.ejb.inventory;

import com.thisal.supply_chain_core.annotation.Audited;
import com.thisal.supply_chain_core.annotation.Console;
import com.thisal.supply_chain_core.entity.InventoryItem;
import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.exception.InventoryItemAlreadyExistsException;
import com.thisal.supply_chain_core.exception.InventoryItemNotFoundException;
import com.thisal.supply_chain_core.mapper.Mapper;
import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.service.InventoryItemService;
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
public class InventoryItemBean implements InventoryItemService {

    @PersistenceContext(unitName = "supply_chainPU")
    private EntityManager entityManager;

    @Inject
    private Mapper mapper;

    @Inject
    @Console
    private Event<String> logEvent;

    @Override
    @PermitAll
    public ResponseModel createInventoryItem(String sku, String name, int qtyOnHand, int reorderThreshold) {
        InventoryItem existingInventoryItem = entityManager.createNamedQuery("InventoryItem.findBySku", InventoryItem.class)
                .setParameter("sku", sku)
                .getResultList().stream().findFirst().orElse(null);
        if (existingInventoryItem != null) {
            logEvent.fire("Inventory Item with " + sku + " is already Registered!");
            throw new InventoryItemAlreadyExistsException("Inventory Item with " + sku + " is already Registered!");
        } else {
            InventoryItem inventoryItem = InventoryItem.builder()
                    .sku(sku)
                    .name(name)
                    .qtyOnHand(qtyOnHand)
                    .reorderThreshold(reorderThreshold)
                    .build();
            try {
                entityManager.persist(inventoryItem);
                entityManager.flush();
                logEvent.fire("Inventory Item created Successfully!");
                return ResponseModel.builder()
                        .status(ResponseStatus.OK)
                        .payload(mapper.toInventoryItemDTO(inventoryItem))
                        .message("Inventory Item created Successfully!")
                        .build();
            } catch (PersistenceException e) {
                logEvent.fire(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @PermitAll
    public ResponseModel updateStock(String sku, int newQty) {
        InventoryItem inventoryItem = entityManager.createNamedQuery("InventoryItem.findBySku", InventoryItem.class)
                .setParameter("sku", sku)
                .getResultList().stream().findFirst().orElse(null);
        if (inventoryItem != null) {
            inventoryItem.setQtyOnHand(inventoryItem.getQtyOnHand() + newQty);
            logEvent.fire("Inventory Item's stock updated Successfully!");
            return ResponseModel.builder()
                    .status(ResponseStatus.OK)
                    .payload(mapper.toInventoryItemDTO(inventoryItem))
                    .message("Inventory Item's stock updated Successfully!")
                    .build();
        } else {
            logEvent.fire("Inventory Item with " + sku + " is not Found!");
            throw new InventoryItemNotFoundException("Inventory Item with " + sku + " is not Found!");
        }
    }

    @Override
    @PermitAll
    public ResponseModel getAllInventoryItems() {
        List<InventoryItem> inventoryItemList = entityManager.createNamedQuery("InventoryItem.findAll", InventoryItem.class)
                .getResultList();
        return ResponseModel.builder()
                .status(ResponseStatus.OK)
                .payload(mapper.toInventoryItemDTOList(inventoryItemList))
                .build();
    }

    @Override
    @PermitAll
    public ResponseModel getLowStockInventoryItems() {
        List<InventoryItem> inventoryItemList = entityManager.createNamedQuery("InventoryItem.findLowStock", InventoryItem.class)
                .getResultList();
        return ResponseModel.builder()
                .status(ResponseStatus.OK)
                .payload(mapper.toInventoryItemDTOList(inventoryItemList))
                .build();
    }

}