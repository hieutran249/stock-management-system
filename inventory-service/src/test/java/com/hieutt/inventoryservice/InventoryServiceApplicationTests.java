package com.hieutt.inventoryservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieutt.inventoryservice.dto.InventoryRequest;
import com.hieutt.inventoryservice.model.Inventory;
import com.hieutt.inventoryservice.repository.InventoryRepository;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {

	@Container
	static MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.32"));

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private InventoryRepository inventoryRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
		dynamicPropertyRegistry.add("spring.datasource.driverClassName", () -> mySQLContainer.getDriverClassName());
		dynamicPropertyRegistry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
		dynamicPropertyRegistry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
	}

	@Test
	void shouldCreateStock() throws Exception {
		// get OrderRequest
		InventoryRequest inventoryRequest = getInventoryRequest();

		String inventoryRequestString = objectMapper.writeValueAsString(inventoryRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory")
						.contentType(MediaType.APPLICATION_JSON)
						.content(inventoryRequestString))
				.andExpect(status().isCreated());

		Assertions.assertEquals(1, inventoryRepository.findAll().size());
	}

	@Test
	void shouldGetInventory() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/inventory"))
				.andExpect(status().isOk());
	}

	@Test
	void shouldCheckIfProductIsInStock() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/inventory/{skuCode}", "skuCode"))
				.andExpect(status().isOk());
	}

	private InventoryRequest getInventoryRequest() {
		return InventoryRequest.builder()
				.skuCode("iphone_13")
				.quantity(10)
				.build();
	}

}
