package com.example.shopappbackend.service;

import com.example.shopappbackend.dto.PageProductDTO;
import com.example.shopappbackend.response.PageResponse;
import com.example.shopappbackend.response.ProductResponse;

public interface ProductRedisService {
    void clear();

    PageResponse<ProductResponse> getAllProducts(PageProductDTO pageProductDTO);

    void saveAllProducts(PageResponse<ProductResponse> pageResponse, PageProductDTO pageProductDTO);
}
