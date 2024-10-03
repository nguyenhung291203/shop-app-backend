package com.example.shopappbackend.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.shopappbackend.dto.ProductImageDTO;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.mapper.ProductImageMapping;
import com.example.shopappbackend.model.Product;
import com.example.shopappbackend.model.ProductImage;
import com.example.shopappbackend.repository.ProductImageRepository;
import com.example.shopappbackend.repository.ProductRepository;
import com.example.shopappbackend.response.ProductImageResponse;
import com.example.shopappbackend.service.ProductImageService;
import com.example.shopappbackend.service.ProductService;
import com.example.shopappbackend.utils.FileServiceUtil;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductService productService;
    private final LocalizationUtil localizationUtil;
    private final FileServiceUtil fileServiceUtil;
    private final ProductRepository productRepository;

    private Product findProductById(long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " product id: " + id)));
    }

    @Override
    public List<ProductImageResponse> getAllProductImagesByProductId(long id) {
        return productImageRepository.findAllByProductId(id).stream()
                .map(ProductImageMapping::mapProductImageToProductImageResponse)
                .toList();
    }

    @Override
    public List<ProductImage> uploadProductImages(Long productId, List<MultipartFile> files) {
        Product product = findProductById(productId);
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String validationError = fileServiceUtil.validateFile(file);
                if (validationError != null) {
                    throw new IllegalArgumentException(validationError);
                }
                String fileName = null;
                try {
                    fileName = fileServiceUtil.storeFile(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                ProductImage productImage = productService.insertProductImage(
                        productId,
                        ProductImageDTO.builder()
                                .productId(productId)
                                .imageUrl(fileName)
                                .build());
                productImages.add(productImage);
                if (product.getThumbnail().isEmpty() && files.indexOf(file) == 0) {
                    product.setThumbnail(fileName);
                    productRepository.save(product);
                }
            }
        }

        return productImages;
    }
}
