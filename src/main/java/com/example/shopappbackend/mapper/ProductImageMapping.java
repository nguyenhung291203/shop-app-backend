package com.example.shopappbackend.mapper;

import com.example.shopappbackend.model.ProductImage;
import com.example.shopappbackend.response.ProductImageResponse;

public class ProductImageMapping {
    public static ProductImageResponse mapProductImageToProductImageResponse(ProductImage productImage) {
        return ProductImageResponse.builder()
                .id(productImage.getId())
                .imageUrl(productImage.getImageUrl())
                .productId(productImage.getProduct().getId())
                .build();
    }
}
