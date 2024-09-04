package com.example.shopappbackend.service.impl;

import com.example.shopappbackend.dto.ProductDTO;
import com.example.shopappbackend.dto.ProductImageDTO;
import com.example.shopappbackend.exception.BadRequestException;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.mapper.ProductMapping;
import com.example.shopappbackend.model.Category;
import com.example.shopappbackend.model.Product;
import com.example.shopappbackend.model.ProductImage;
import com.example.shopappbackend.repository.CategoryRepository;
import com.example.shopappbackend.repository.ProductImageRepository;
import com.example.shopappbackend.repository.ProductRepository;
import com.example.shopappbackend.response.PageResponse;
import com.example.shopappbackend.response.ProductResponse;
import com.example.shopappbackend.service.ProductService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final LocalizationUtil localizationUtil;
    private final RedisTemplate redisTemplate;
    private final Gson gson;

    @Override
    public PageResponse<ProductResponse> getAllProducts(String search, Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(search, pageable);

        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(ProductMapping::mapProductToProductResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProductResponse>builder()
                .contents(productResponses)
                .numberOfElements(productPage.getNumberOfElements())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .build();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository
                .findById(id).orElseThrow(() ->
                        new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " product id: " + id)));
        return ProductMapping.mapProductToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProductsByIds(List<Long> ids) {
        List<Product> products = ids.isEmpty() ? productRepository.findAll() : productRepository.findAllById(ids);
        return products.stream()
                .map(ProductMapping::mapProductToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse insertProduct(ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()
                        -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " category id: " + productDTO.getCategoryId())));
        Product product = Product.builder()
                .name(productDTO.getName())
                .thumbnail(productDTO.getDescription())
                .description(productDTO.getDescription())
                .category(category)
                .price(productDTO.getPrice())
                .build();

        return ProductMapping.mapProductToProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()
                        -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " category id: " + productDTO.getCategoryId())));
        Product product = productRepository.findById(id)
                .orElseThrow(()
                        -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " product id: " + id)));
        product.setCategory(category);
        product.setDescription(productDTO.getDescription());
        product.setThumbnail(productDTO.getThumbnail());
        product.setPrice(productDTO.getPrice());
        product.setName(productDTO.getName());
        return ProductMapping.mapProductToProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        if (this.productRepository.findAll().isEmpty())
            throw new BadRequestException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND));
        if (!this.productRepository.existsById(id))
            throw new BadRequestException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " product id: " + id));
        this.productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductImage insertProductImage(Long productId, ProductImageDTO productImageDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new NotFoundException(localizationUtil
                                .getLocaleResolver(MessageKey.NOT_FOUND, " product id: " + productId)));
        List<ProductImage> productImages = productImageRepository.findAllByProductId(productId);
        if (productImages.size() >= 5)
            throw new BadRequestException(localizationUtil.getLocaleResolver(MessageKey.EXCEED_QUANTITY));
        ProductImage productImage = ProductImage.builder()
                .imageUrl(productImageDTO.getImageUrl())
                .product(product)
                .build();
        return productImageRepository.save(productImage);
    }

    @Override
    @Transactional
    public void generateFakeProducts() {
        Faker faker = new Faker();
        for (int i = 0; i < 500; i++) {
            String productName = faker.commerce().productName();
            if (productRepository.existsByName(productName))
                continue;
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price(faker.number().numberBetween(10, 1000))
                    .description(faker.lorem().sentence())
                    .categoryId((long) faker.number().numberBetween(2, 5))
                    .build();
            insertProduct(productDTO);
        }
    }
}
