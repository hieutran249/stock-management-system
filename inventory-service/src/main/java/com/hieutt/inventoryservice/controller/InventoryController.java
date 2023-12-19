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

    @GetMapping("/is-in-stock")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCodes) {
        return inventoryService.isInStock(skuCodes);
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
