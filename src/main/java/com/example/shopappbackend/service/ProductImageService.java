package com.example.shopappbackend.service;

import com.example.shopappbackend.model.ProductImage;
import com.example.shopappbackend.response.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    List<ProductImageResponse> getAllProductImagesByProductId(long id);
    List<ProductImage> uploadProductImages(Long productId, List<MultipartFile> files);
}
