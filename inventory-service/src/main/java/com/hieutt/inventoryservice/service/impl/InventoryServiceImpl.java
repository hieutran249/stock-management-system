package com.hieutt.inventoryservice.service.impl;

import com.hieutt.inventoryservice.dto.InventoryRequest;
import com.hieutt.inventoryservice.dto.InventoryResponse;
import com.hieutt.inventoryservice.model.Inventory;
import com.hieutt.inventoryservice.repository.InventoryRepository;
import com.hieutt.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
//    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
//        log.info("WAIT STARTED");
//        Thread.sleep(10000);
//        log.info("WAIT ENDED");

        return inventoryRepository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void addStock(InventoryRequest inventoryRequest) {
        Inventory inventory = Inventory.builder()
                .skuCode(inventoryRequest.getSkuCode())
                .quantity(inventoryRequest.getQuantity())
                .build();

        inventoryRepository.save(inventory);
    }

    @Override
    public List<InventoryResponse> getInventory() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(this::mapToDto)
                .toList();
    }

    private InventoryResponse mapToDto(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .skuCode(inventory.getSkuCode())
                .quantity(inventory.getQuantity())
                .isInStock(inventory.getQuantity() > 0)
                .build();
    }


}
