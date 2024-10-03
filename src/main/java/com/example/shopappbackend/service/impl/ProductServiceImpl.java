package com.example.shopappbackend.service.impl;

import com.example.shopappbackend.dto.PageProductDTO;
import com.example.shopappbackend.dto.ProductDTO;
import com.example.shopappbackend.dto.ProductImageDTO;
import com.example.shopappbackend.exception.BadRequestException;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.mapper.ProductMapping;
import com.example.shopappbackend.model.Category;
import com.example.shopappbackend.model.Product;
import com.example.shopappbackend.model.ProductImage;
import com.example.shopappbackend.repository.CategoryRepository;
import com.example.shopappbackend.repository.OrderDetailRepository;
import com.example.shopappbackend.repository.ProductImageRepository;
import com.example.shopappbackend.repository.ProductRepository;
import com.example.shopappbackend.response.PageResponse;
import com.example.shopappbackend.response.ProductResponse;
import com.example.shopappbackend.service.ProductService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import com.example.shopappbackend.utils.PageRequestUtil;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final LocalizationUtil localizationUtil;
    private final ProductMapping productMapping;

    private Product findProductById(long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " product id: " + id)));
    }

    private Category findCategoryById(long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " category id: " + id)));
    }

    private ProductResponse mapProductToProductResponse(Product product) {
        return productMapping.mapProductToProductResponse(
                product,
                this.productImageRepository.findAllByProductId(product.getId()).stream()
                        .map(ProductImage::getImageUrl)
                        .toList());
    }

    @Override
    public PageResponse<ProductResponse> getAllProducts(PageProductDTO pageProductDTO) {
        Page<Product> productPage = productRepository.findByKeywordAndCategory(
                pageProductDTO.getKeyword().trim(),
                pageProductDTO.getCategoryId(),
                PageRequestUtil.getPageable(pageProductDTO));
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::mapProductToProductResponse)
                .toList();
        return PageResponse.<ProductResponse>builder()
                .contents(productResponses)
                .numberOfElements(productPage.getNumberOfElements())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .build();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = this.findProductById(id);
        return this.mapProductToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProductsByIds(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        return products.stream().map(this::mapProductToProductResponse).toList();
    }

    @Override
    @Transactional
    public ProductResponse insertProduct(ProductDTO productDTO) {
        Category category = this.findCategoryById(productDTO.getCategoryId());
        if (productRepository.existsByName(productDTO.getName()))
            throw new BadRequestException("Tên sản phẩm đã tồn tại");
        Product product = Product.builder()
                .name(productDTO.getName())
                .thumbnail(productDTO.getDescription())
                .description(productDTO.getDescription())
                .category(category)
                .price(productDTO.getPrice())
                .sold(productDTO.getSold())
                .quantity(productDTO.getQuantity())
                .rating(productDTO.getRating())
                .build();

        return this.mapProductToProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductDTO productDTO) {
        Product product = this.findProductById(id);
        Category category = this.findCategoryById(productDTO.getCategoryId());
        product.setCategory(category);
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getThumbnail() != null) {
            product.setThumbnail(productDTO.getThumbnail());
        }
        if (productDTO.getPrice() > 0) {
            product.setPrice(productDTO.getPrice());
        }
        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        return this.mapProductToProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        Product product = this.findProductById(id);
        if (this.orderDetailRepository.existsByProductId(id))
            throw new BadRequestException("Không thể xóa sản phẩm này ở trong thông tin hóa đơn khách hàng");
        this.productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductImage insertProductImage(Long productId, ProductImageDTO productImageDTO) {
        Product product = this.findProductById(productId);
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
        for (int i = 0; i < 100; i++) {
            String productName = faker.commerce().productName();
            if (productRepository.existsByName(productName)) continue;
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price(faker.number().numberBetween(10, 1000))
                    .quantity(faker.number().numberBetween(100, 1000))
                    .description(faker.lorem().sentence())
                    .categoryId((long) faker.number().numberBetween(2, 5))
                    .build();
            insertProduct(productDTO);
        }
    }
}
