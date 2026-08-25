package com.thisal.supply_chain_core.entity;

import com.thisal.supply_chain_core.enums.StockStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "InventoryItem.findAll", query = "SELECT it FROM InventoryItem it"),
        @NamedQuery(name = "InventoryItem.findBySku", query = "SELECT it FROM InventoryItem it WHERE it.sku=:sku"),
        @NamedQuery(name = "InventoryItem.findLowStock", query = "SELECT it FROM InventoryItem it WHERE it.qtyOnHand < it.reorderThreshold")
})
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID _id;

    @Column(unique = true, nullable = false, length = 50)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "qty_on_hand", nullable = false)
    private int qtyOnHand;

    @Column(name = "reorder_threshold", nullable = false)
    private int reorderThreshold;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status", nullable = false, length = 15)
    private StockStatus stockStatus;

    @Setter
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.stockStatus = computeStockStatus();
        this.updatedAt = LocalDateTime.now();
    }

    private StockStatus computeStockStatus() {
        if (qtyOnHand <= 0) return StockStatus.OUT_OF_STOCK;
        else if (qtyOnHand < reorderThreshold) return StockStatus.LOW_STOCK;
        else return StockStatus.IN_STOCK;
    }

}