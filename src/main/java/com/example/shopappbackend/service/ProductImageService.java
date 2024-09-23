package com.example.shopappbackend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.shopappbackend.model.ProductImage;
import com.example.shopappbackend.response.ProductImageResponse;

public interface ProductImageService {
    List<ProductImageResponse> getAllProductImagesByProductId(long id);

    List<ProductImage> uploadProductImages(Long productId, List<MultipartFile> files);
}
