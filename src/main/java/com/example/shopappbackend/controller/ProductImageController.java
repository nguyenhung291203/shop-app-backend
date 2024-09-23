package com.example.shopappbackend.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopappbackend.response.ResponseApi;
import com.example.shopappbackend.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/product-images")
public class ProductImageController {
    private final ProductImageService productImageService;

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getAllProductImagesByProductId(@PathVariable @Valid long productId) {
        return ResponseEntity.ok(ResponseApi.builder()
                .data(productImageService.getAllProductImagesByProductId(productId))
                .message("")
                .build());
    }
}
