package com.hieutt.orderservice.service;

import com.hieutt.orderservice.dto.OrderRequest;
import com.hieutt.orderservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    String placeOrder(OrderRequest orderRequest);

    List<OrderResponse> getAllOrders();
}
