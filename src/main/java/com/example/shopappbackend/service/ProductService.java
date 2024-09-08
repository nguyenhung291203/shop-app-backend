package com.example.shopappbackend.service;

import com.example.shopappbackend.dto.PageProductDTO;
import com.example.shopappbackend.dto.ProductDTO;
import com.example.shopappbackend.dto.ProductImageDTO;
import com.example.shopappbackend.model.Product;
import com.example.shopappbackend.model.ProductImage;
import com.example.shopappbackend.response.PageResponse;
import com.example.shopappbackend.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    PageResponse<ProductResponse> getAllProducts(PageProductDTO pageProductDTO);

    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProductsByIds(List<Long> ids);

    ProductResponse insertProduct(ProductDTO productDTO);

    ProductResponse updateProduct(Long id, ProductDTO productDTO);

    void deleteProductById(Long id);

    ProductImage insertProductImage(Long productId, ProductImageDTO productImageDTO);
    void generateFakeProducts();
}
