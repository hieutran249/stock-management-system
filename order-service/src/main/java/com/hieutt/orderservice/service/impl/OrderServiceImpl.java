package com.hieutt.orderservice.service.impl;

import com.hieutt.orderservice.dto.OrderLineItemDto;
import com.hieutt.orderservice.dto.OrderRequest;
import com.hieutt.orderservice.dto.OrderResponse;
import com.hieutt.orderservice.model.Order;
import com.hieutt.orderservice.model.OrderLineItem;
import com.hieutt.orderservice.repository.OrderRepository;
import com.hieutt.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    @Override
    @Transactional
    public void placeOrder(OrderRequest orderRequest) {
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToEntityOrderItem)
                .toList();

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItems)
                .build();

        // setting order attribute of each orderLineItems
        order.getOrderLineItems().forEach(item -> item.setOrder(order));

        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToDtoOrder).toList();
    }

    private OrderLineItem mapToEntityOrderItem(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .skuCode(orderLineItemDto.getSkuCode())
                .price(orderLineItemDto.getPrice())
                .quantity(orderLineItemDto.getQuantity())
                .build();
    }

    private OrderLineItemDto mapToDtoOrderItem(OrderLineItem orderLineItem) {
        return OrderLineItemDto.builder()
                .id(orderLineItem.getId())
                .skuCode(orderLineItem.getSkuCode())
                .price(orderLineItem.getPrice())
                .quantity(orderLineItem.getQuantity())
                .build();
    }

    private OrderResponse mapToDtoOrder(Order order) {
        List<OrderLineItemDto> orderLineItemDtoList = order.getOrderLineItems().stream().map(this::mapToDtoOrderItem).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderLineItemDtoList(orderLineItemDtoList)
                .build();
    }
}
