package com.example.shopappbackend.service.impl;

import com.example.shopappbackend.dto.PageProductDTO;
import com.example.shopappbackend.response.PageResponse;
import com.example.shopappbackend.response.ProductResponse;
import com.example.shopappbackend.service.ProductRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductRedisServiceImpl implements ProductRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    private String getKeyFrom(PageProductDTO productDTO) {
        Long categoryId = productDTO.getCategoryId();
        String pageRequest = productDTO.toString();
        return String.format("all_products:category_id:%s-%s", categoryId, pageRequest);
    }

    @Override
    public void clear() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
    }

    @Override
    public PageResponse<ProductResponse> getAllProducts(PageProductDTO pageProductDTO) {
        String key = this.getKeyFrom(pageProductDTO);
        String json = (String) redisTemplate.opsForValue().get(key);
        try {
            return json!=null?redisObjectMapper.readValue(json, new TypeReference<PageResponse<ProductResponse>>() {
            }):null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAllProducts(PageResponse<ProductResponse> pageResponse, PageProductDTO pageProductDTO) {
        String key = this.getKeyFrom(pageProductDTO);
        String json = null;
        try {
            json = redisObjectMapper.writeValueAsString(pageResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForValue().set(key, json);
    }

}

