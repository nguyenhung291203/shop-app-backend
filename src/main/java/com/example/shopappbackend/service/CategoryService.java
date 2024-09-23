package com.example.shopappbackend.service;

import java.util.List;

import org.apache.coyote.BadRequestException;

import com.example.shopappbackend.dto.CategoryDTO;
import com.example.shopappbackend.model.Category;

public interface CategoryService {
    Category insertCategory(CategoryDTO category) throws BadRequestException;

    Category getCategoryById(long id);

    List<Category> getAllCategories();

    Category updateCategory(long id, CategoryDTO category);

    void deleteCategory(long id);
}
