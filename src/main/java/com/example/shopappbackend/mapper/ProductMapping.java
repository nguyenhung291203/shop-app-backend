package com.example.shopappbackend.mapper;

import com.example.shopappbackend.model.Product;
import com.example.shopappbackend.response.ProductResponse;
import com.example.shopappbackend.utils.UrlProductImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMapping {
    private final UrlProductImageUtil urlProductImageUtil;

    public ProductResponse mapProductToProductResponse(Product product, List<String> images) {
        ProductResponse productResponse = ProductResponse.builder()
                .categoryId(product.getCategory().getId())
                .description(product.getDescription())
                .thumbnail(urlProductImageUtil.generateUrlProductImage(product.getThumbnail()))
                .price(product.getPrice())
                .name(product.getName())
                .quantity(product.getQuantity())
                .rating(product.getRating())
                .id(product.getId())
                .sold(product.getSold())
                .images(images.stream().map(urlProductImageUtil::generateUrlProductImage).toList())
                .build();
        productResponse.setCreatedDate(product.getCreatedDate());
        productResponse.setUpdatedDate(product.getUpdatedDate());
        return productResponse;
    }
}