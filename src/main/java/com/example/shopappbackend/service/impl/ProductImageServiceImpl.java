package com.example.shopappbackend.service.impl;

import com.example.shopappbackend.mapper.ProductImageMapping;
import com.example.shopappbackend.repository.ProductImageRepository;
import com.example.shopappbackend.response.ProductImageResponse;
import com.example.shopappbackend.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;

    @Override
    public List<ProductImageResponse> getAllProductImagesByProductId(long id) {
        return productImageRepository.findAllByProductId(id)
                .stream().map(ProductImageMapping::mapProductImageToProductImageResponse)
                .collect(Collectors.toList());
    }
}
