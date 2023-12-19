package com.hieutt.orderservice.service.impl;

import com.hieutt.orderservice.dto.InventoryResponse;
import com.hieutt.orderservice.dto.OrderLineItemDto;
import com.hieutt.orderservice.dto.OrderRequest;
import com.hieutt.orderservice.dto.OrderResponse;
import com.hieutt.orderservice.event.OrderPlacedEvent;
import com.hieutt.orderservice.model.Order;
import com.hieutt.orderservice.model.OrderLineItem;
import com.hieutt.orderservice.repository.OrderRepository;
import com.hieutt.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Override
    @Transactional
    public String placeOrder(OrderRequest orderRequest) {
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

        List<String> skuCodes = order.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        // call Inventory Service, and place order if product is in stock
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/v1/inventory/is-in-stock",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class) // return type of the uri
                .block(); // synchronous communication

        boolean allProductIsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::getIsInStock);

        if (allProductIsInStock) {
            orderRepository.save(order);
            // send OrderPlacedEvent obj as a message to the notification topic
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed successfully";
        }
        else throw new IllegalArgumentException("The product is not in stock! Please try again later!");
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
