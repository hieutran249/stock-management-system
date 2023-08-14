package com.hieutt.inventoryservice.service;

import com.hieutt.inventoryservice.dto.InventoryRequest;
import com.hieutt.inventoryservice.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    boolean isInStock(String skuCode);

    void addStock(InventoryRequest inventoryRequest);

    List<InventoryResponse> getInventory();
}
