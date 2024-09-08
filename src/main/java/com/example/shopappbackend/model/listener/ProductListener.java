package com.example.shopappbackend.model.listener;

import com.example.shopappbackend.model.Product;
import com.example.shopappbackend.service.ProductRedisService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class ProductListener {
    private final ProductRedisService productRedisService;
    private static final Logger logger = LoggerFactory.getLogger(ProductListener.class);

    @PrePersist
    public void prePersist(Product product) {
        logger.info("Pre persist product: {}", product);
    }

    @PostPersist
    public void postPersist(Product product) {
        logger.info("Post persist product: {}", product);
        productRedisService.clear();
    }

    @PreUpdate
    public void preUpdate(Product product) {
        logger.info("Pre update product: {}", product);
    }

    @PostUpdate
    public void postUpdate(Product product) {
        logger.info("Post update product: {}", product);
        productRedisService.clear();
    }

    @PreRemove
    public void preRemove(Product product) {
        logger.info("Pre delete product: {}", product);
    }

    @PostRemove
    public void postRemove(Product product) {
        logger.info("Post delete product: {}", product);
        productRedisService.clear();
    }
}
