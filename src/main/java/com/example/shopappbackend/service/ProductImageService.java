package com.example.shopappbackend.service;

import com.example.shopappbackend.model.ProductImage;
import com.example.shopappbackend.response.ProductImageResponse;

import java.util.List;

public interface ProductImageService {
    List<ProductImageResponse> getAllProductImagesByProductId(long id);
}
