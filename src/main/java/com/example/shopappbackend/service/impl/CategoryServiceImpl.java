package com.example.shopappbackend.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.shopappbackend.dto.CategoryDTO;
import com.example.shopappbackend.exception.BadRequestException;
import com.example.shopappbackend.exception.DataIntegrityViolationException;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.model.Category;
import com.example.shopappbackend.repository.CategoryRepository;
import com.example.shopappbackend.service.CategoryService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final LocalizationUtil localizationUtil;
    private final RedisTemplate redisTemplate;
    private final Gson gson = new Gson();

    @Override
    public Category insertCategory(CategoryDTO category) throws BadRequestException {
        if (categoryRepository.existsByName(category.getName()))
            throw new DataIntegrityViolationException(
                    localizationUtil.getLocaleResolver(MessageKey.CATEGORY_ALREADY_EXIST));
        Category categoryNew = Category.builder().name(category.getName()).build();
        return this.categoryRepository.save(categoryNew);
    }

    @Override
    public Category getCategoryById(long id) {
        return this.categoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " category id:" + id)));
    }

    @Override
    public List<Category> getAllCategories() {
        String dataRedis = (String) redisTemplate.opsForValue().get(Category.CategoryRedis);
        List<Category> categories;
        if (dataRedis == null) {
            categories = this.categoryRepository.findAll();
            if (categories.isEmpty())
                throw new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND));
            String data = gson.toJson(categories);
            redisTemplate.opsForValue().set(Category.CategoryRedis, data);
        } else {
            Type listType = new TypeToken<List<Category>>() {}.getType();
            categories = gson.fromJson(dataRedis, listType);
        }
        return categories;
    }

    @Override
    public Category updateCategory(long id, CategoryDTO category) {
        Category categoryNew = this.getCategoryById(id);
        categoryNew.setName(category.getName());
        return categoryRepository.save(categoryNew);
    }

    @Override
    public void deleteCategory(long id) {
        if (this.categoryRepository.findAll().isEmpty())
            throw new BadRequestException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND));
        if (this.categoryRepository.findById(id).isEmpty())
            throw new NotFoundException(
                    localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " category id: " + id));
        this.categoryRepository.deleteById(id);
    }
}
