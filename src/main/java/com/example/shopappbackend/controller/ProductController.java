package com.example.shopappbackend.controller;

import com.example.shopappbackend.dto.PageProductDTO;
import com.example.shopappbackend.dto.ProductDTO;
import com.example.shopappbackend.dto.ProductImageDTO;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.model.ProductImage;
import com.example.shopappbackend.response.PageResponse;
import com.example.shopappbackend.response.ProductResponse;
import com.example.shopappbackend.response.ResponseApi;
import com.example.shopappbackend.service.ProductRedisService;
import com.example.shopappbackend.service.ProductService;
import com.example.shopappbackend.utils.FileServiceUtil;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/products")
@Validated
@CrossOrigin
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final FileServiceUtil fileServiceUtil;
    private final LocalizationUtil localizationUtil;
    private final ProductRedisService productRedisService;

    @PostMapping("/search")
    public ResponseEntity<?> getAllProducts(@Valid @RequestBody PageProductDTO pageProductDTO) {
        PageResponse<ProductResponse> response = productRedisService.getAllProducts(pageProductDTO);
        if (response == null) {
            response = productService.getAllProducts(pageProductDTO);
            productRedisService.saveAllProducts(response, pageProductDTO);
        }

        return ResponseEntity.ok(ResponseApi.builder()
                .data(response)
                .message(localizationUtil.getLocaleResolver(MessageKey.PRODUCT_GET_SUCCESSFULLY))
                .build());

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(ResponseApi.builder().data(productService.getProductById(id)).message(localizationUtil.getLocaleResolver(MessageKey.PRODUCT_GET_SUCCESSFULLY)).build());
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getAllProductsByIds(@RequestParam @Valid List<Long> ids) {
        return ResponseEntity.ok(ResponseApi.builder()
                .message(localizationUtil.getLocaleResolver(MessageKey.PRODUCT_GET_SUCCESSFULLY))
                .data(productService.getAllProductsByIds(ids))
                .build());
    }

    @PostMapping("")
    public ResponseEntity<?> insertProduct(@Valid @RequestBody ProductDTO productDTO) {
        return new ResponseEntity<>(ResponseApi.builder().message(localizationUtil.getLocaleResolver(MessageKey.PRODUCT_INSERT_SUCCESSFULLY)).data(productService.insertProduct(productDTO)).build(), HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}/upload-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> insertProductImage(@Valid @PathVariable Long id, @ModelAttribute("files") List<MultipartFile> files) {
        try {
            files = files.isEmpty() ? new ArrayList<>() : files;
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String validationError = fileServiceUtil.validateFile(file);
                    if (validationError != null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
                    }
                    String fileName = fileServiceUtil.storeFile(file);

                    ProductImage productImage = productService.insertProductImage(id, ProductImageDTO.builder().productId(id).imageUrl(fileName).build());
                    productImages.add(productImage);
                }
            }
            return new ResponseEntity<>(ResponseApi.builder()
                    .data(productImages)
                    .message(localizationUtil.getLocaleResolver(MessageKey.PRODUCT_IMAGE_UPLOAD_SUCCESSFULLY))
                    .build(), HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error storing file");
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
            } else throw new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND));
        } catch (Exception e) {
            throw new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@Valid @PathVariable long id, @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(ResponseApi.builder()
                .message(localizationUtil.getLocaleResolver(MessageKey.PRODUCT_UPDATE_SUCCESSFULLY))
                .data(productService.updateProduct(id, productDTO))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@Valid @PathVariable long id) {
        productService.deleteProductById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseApi.builder()
                        .message(localizationUtil.getLocaleResolver(MessageKey.PRODUCT_DELETE_SUCCESSFULLY))
                        .data(null)
                        .build());
    }

    //    @PostMapping("/generateFakeProducts")
    private ResponseEntity<?> generateFakeProducts() {
        productService.generateFakeProducts();
        return ResponseEntity.ok("Success");
    }
}
