package com.hieutt.productservice.service.impl;

import com.hieutt.productservice.dto.ProductRequest;
import com.hieutt.productservice.dto.ProductResponse;
import com.hieutt.productservice.model.Product;
import com.hieutt.productservice.repository.ProductRepository;
import com.hieutt.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Override
    @Transactional
    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);

        // logging
        log.info("Product {} added", product.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToDto)
                .toList();
    }

    // map Entity into DTO
    private ProductResponse mapToDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

//    // map DTO into Model
//    private Product mapToModel(ProductResponse productResponse) {
//        // Mapping using ModelMapper
//        Product product = mapper.map(productResponse, Product.class);
//
////        Comment comment = new Comment();
////        comment.setBody(commentDto.getBody());
//
//        return product;
//    }
}
