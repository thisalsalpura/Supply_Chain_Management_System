package com.thisal.supply_chain_ejb.ejb.inventory;

import com.thisal.supply_chain_core.annotation.Console;
import com.thisal.supply_chain_core.entity.InventoryItem;
import com.thisal.supply_chain_core.model.LowStockAlertModel;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

@Singleton
@Startup
public class InventoryMonitorBean {

    @PersistenceContext(unitName = "supply_chainPU")
    private EntityManager entityManager;

    @Inject
    @Console
    private Event<String> logEvent;

    @Inject
    private Event<LowStockAlertModel> lowStockAlertEvent;

    @Schedule(hour = "*", minute = "*/5", persistent = false)
    public void checkLowStock() {
        List<InventoryItem> inventoryItemList = entityManager.createNamedQuery("InventoryItem.findLowStock", InventoryItem.class)
                .getResultList();
        if (inventoryItemList.isEmpty()) {
            logEvent.fire("No low stock inventory items Found!");
        } else {
            StringBuilder stringBuilder = new StringBuilder(inventoryItemList.size() + " low stock inventory items Found!\n");
            for (InventoryItem inventoryItem : inventoryItemList) {
                stringBuilder.append("| SKU: ").append(inventoryItem.getSku())
                        .append(" | Name: ").append(inventoryItem.getName())
                        .append(" | Qty On Hand: ").append(inventoryItem.getQtyOnHand())
                        .append(" | Reorder Threshold: ").append(inventoryItem.getReorderThreshold())
                        .append(" |");
            }
            logEvent.fire(stringBuilder.toString());
            List<String> skuList = inventoryItemList.stream().map(InventoryItem::getSku).toList();
            lowStockAlertEvent.fire(LowStockAlertModel.builder()
                    .affectedSkus(skuList)
                    .detectedAt(LocalDateTime.now())
                    .build());
        }
    }

}