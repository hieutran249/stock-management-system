package com.hieutt.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InventoryResponse {
    private Long id;
    private String skuCode;
    private Integer quantity;
    private Boolean isInStock;
}
