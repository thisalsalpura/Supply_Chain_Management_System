package com.thisal.supply_chain_core.mapper;

import com.thisal.supply_chain_core.dto.InventoryItemDTO;
import com.thisal.supply_chain_core.dto.OrderDTO;
import com.thisal.supply_chain_core.dto.VendorDTO;
import com.thisal.supply_chain_core.entity.InventoryItem;
import com.thisal.supply_chain_core.entity.Order;
import com.thisal.supply_chain_core.entity.Vendor;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "cdi")
public interface Mapper {

    VendorDTO toVendorDTO(Vendor vendor);

    List<VendorDTO> toVendorDTOList(List<Vendor> vendors);

    InventoryItemDTO toInventoryItemDTO(InventoryItem inventoryItem);

    List<InventoryItemDTO> toInventoryItemDTOList(List<InventoryItem> inventoryItems);

    OrderDTO toOrderDTO(Order order);

    List<OrderDTO> toOrderDTOList(List<Order> orders);

}