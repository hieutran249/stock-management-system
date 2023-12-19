package com.hieutt.inventoryservice.service;

import com.hieutt.inventoryservice.dto.InventoryRequest;
import com.hieutt.inventoryservice.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> isInStock(List<String> skuCodes);

    void addStock(InventoryRequest inventoryRequest);

    List<InventoryResponse> getInventory();
}
