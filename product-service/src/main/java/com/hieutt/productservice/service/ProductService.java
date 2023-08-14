package com.hieutt.productservice.service;

import com.hieutt.productservice.dto.ProductRequest;
import com.hieutt.productservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    void createProduct(ProductRequest productRequest);

    List<ProductResponse> getAllProducts();
}
