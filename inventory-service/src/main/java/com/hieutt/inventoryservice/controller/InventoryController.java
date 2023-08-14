package com.hieutt.inventoryservice.controller;

import com.hieutt.inventoryservice.dto.InventoryRequest;
import com.hieutt.inventoryservice.dto.InventoryResponse;
import com.hieutt.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable(value = "skuCode") String skuCode) {
        return inventoryService.isInStock(skuCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addStock(@RequestBody InventoryRequest inventoryRequest) {
        inventoryService.addStock(inventoryRequest);
        return "Stock added successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> getInventory() {
        return inventoryService.getInventory();
    }
}
