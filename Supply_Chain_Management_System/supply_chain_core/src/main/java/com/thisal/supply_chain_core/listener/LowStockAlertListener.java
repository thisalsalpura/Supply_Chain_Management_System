package com.thisal.supply_chain_core.listener;

import com.thisal.supply_chain_core.model.LowStockAlertModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class LowStockAlertListener {

    public void onLowStockAlert(@Observes LowStockAlertModel lowStockAlertModel) {
        System.out.println("Low stock alert received for " + lowStockAlertModel.getAffectedSkus() + " SKUs at " + lowStockAlertModel.getDetectedAt() + "!");
    }

}