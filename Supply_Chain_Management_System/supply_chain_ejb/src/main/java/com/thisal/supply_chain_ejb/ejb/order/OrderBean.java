package com.thisal.supply_chain_ejb.ejb.order;

import com.thisal.supply_chain_core.annotation.Audited;
import com.thisal.supply_chain_core.annotation.Console;
import com.thisal.supply_chain_core.dto.OrderRequestDTO;
import com.thisal.supply_chain_core.entity.InventoryItem;
import com.thisal.supply_chain_core.entity.Order;
import com.thisal.supply_chain_core.entity.OrderItem;
import com.thisal.supply_chain_core.entity.Vendor;
import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.exception.InsufficientStockException;
import com.thisal.supply_chain_core.exception.InventoryItemNotFoundException;
import com.thisal.supply_chain_core.exception.VendorNotFoundException;
import com.thisal.supply_chain_core.mapper.Mapper;
import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.service.OrderService;
import com.thisal.supply_chain_ejb.ejb.audit.AuditLogBean;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
@Audited
public class OrderBean implements OrderService {

    @PersistenceContext(unitName = "supply_chainPU")
    private EntityManager entityManager;

    @EJB
    private AuditLogBean auditLogBean;

    @Inject
    private Mapper mapper;

    @Inject
    @Console
    private Event<String> logEvent;

    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ResponseModel placeOrder(String vendorEmail, List<OrderRequestDTO> orderRequestDTOs) {
        Vendor vendor = entityManager.createNamedQuery("Vendor.findByEmail", Vendor.class)
                .setParameter("email", vendorEmail.trim().toLowerCase())
                .getResultList().stream().findFirst().orElse(null);
        if (vendor != null) {
            Order order = Order.builder()
                    .vendor(vendor)
                    .build();
            for (OrderRequestDTO orderRequestDTO : orderRequestDTOs) {
                InventoryItem inventoryItem = entityManager.createNamedQuery("InventoryItem.findBySku", InventoryItem.class)
                        .setParameter("sku", orderRequestDTO.getSku())
                        .getResultList().stream().findFirst().orElseThrow(() -> {
                            logEvent.fire("Inventory Item with " + orderRequestDTO.getSku() + " is not Found!");
                            auditLogBean.logFailedOperation("Place Order", "Inventory Item with " + orderRequestDTO.getSku() + " is not Found!");
                            return new InventoryItemNotFoundException("Inventory Item with " + orderRequestDTO.getSku() + " is not Found!");
                        });
                if (inventoryItem.getQtyOnHand() < orderRequestDTO.getQty()) {
                    logEvent.fire("Inventory Item with " + inventoryItem.getSku() + " is only have " + inventoryItem.getQtyOnHand() + " Items!");
                    auditLogBean.logFailedOperation("Place Order", "Inventory Item with " + inventoryItem.getSku() + " is only have " + inventoryItem.getQtyOnHand() + " Items!");
                    throw new InsufficientStockException("Inventory Item with " + inventoryItem.getSku() + " is only have " + inventoryItem.getQtyOnHand() + " Items!");
                } else {
                    inventoryItem.setQtyOnHand(inventoryItem.getQtyOnHand() - orderRequestDTO.getQty());
                    OrderItem orderItem = OrderItem.builder()
                            .inventoryItem(inventoryItem)
                            .qty(orderRequestDTO.getQty())
                            .build();
                    order.addOrderItem(orderItem);
                }
            }
            entityManager.persist(order);
            entityManager.flush();
            logEvent.fire("Order placed Successfully!");
            return ResponseModel.builder()
                    .status(ResponseStatus.OK)
                    .payload(mapper.toOrderDTO(order))
                    .message("Order placed Successfully!")
                    .build();
        } else {
            logEvent.fire("Vendor with " + vendorEmail + " is not Found!");
            auditLogBean.logFailedOperation("Place Order", "Vendor with " + vendorEmail + " is not Found!");
            throw new VendorNotFoundException("Vendor with " + vendorEmail + " is not Found!");
        }
    }

    @Override
    @PermitAll
    public ResponseModel getAllOrders() {
        List<Order> orderList = entityManager.createNamedQuery("Order.findAll", Order.class)
                .getResultList();
        return ResponseModel.builder()
                .status(ResponseStatus.OK)
                .payload(mapper.toOrderDTOList(orderList))
                .build();
    }

}