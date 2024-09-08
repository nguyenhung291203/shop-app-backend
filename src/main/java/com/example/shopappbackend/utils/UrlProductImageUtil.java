package com.example.shopappbackend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlProductImageUtil {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${api.prefix}")
    private String apiPrefix;

    public String generateUrlProductImage(String image) {
        return baseUrl + apiPrefix + "/products/images/" + image;
    }
}
