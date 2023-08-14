package com.hieutt.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieutt.orderservice.dto.OrderLineItemDto;
import com.hieutt.orderservice.dto.OrderRequest;
import com.hieutt.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.32"));

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderRepository orderRepository;

	@DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        dynamicPropertyRegistry.add("spring.datasource.driverClassName", () -> mySQLContainer.getDriverClassName());
        dynamicPropertyRegistry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        dynamicPropertyRegistry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
    }

    @Test
    void shouldCreateOrder() throws Exception {
        // get OrderRequest
        OrderRequest orderRequest = getOrderRequest();

        String orderRequestString = objectMapper.writeValueAsString(orderRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestString))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, orderRepository.findAll().size());
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders"))
                .andExpect(status().isOk());
    }

    private OrderRequest getOrderRequest() {
        OrderLineItemDto[] items = new OrderLineItemDto[]{getOrderItemDto()};

        return OrderRequest.builder()
                .orderLineItemDtoList(Arrays.asList(items))
                .build();
    }

    private OrderLineItemDto getOrderItemDto() {
        return OrderLineItemDto.builder()
                .skuCode("iphone_13")
                .price(BigDecimal.valueOf(12000))
                .quantity(1)
                .build();
    }

}
