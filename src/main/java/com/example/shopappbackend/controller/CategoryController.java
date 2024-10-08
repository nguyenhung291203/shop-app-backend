package com.example.shopappbackend.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.shopappbackend.dto.CategoryDTO;
import com.example.shopappbackend.model.Category;
import com.example.shopappbackend.response.ResponseApi;
import com.example.shopappbackend.service.CategoryService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/categories")
@Validated
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtil localizationUtil;

    @GetMapping("")
    public ResponseEntity<ResponseApi> getAllCategories(@RequestParam Map<String, Object> params) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseApi.builder()
                .data(categories)
                .message(localizationUtil.getLocaleResolver(MessageKey.CATEGORY_GET_SUCCESSFULLY))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseApi> getCategoryById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(ResponseApi.builder()
                .message(localizationUtil.getLocaleResolver(MessageKey.CATEGORY_GET_SUCCESSFULLY, " id: " + id))
                .data(categoryService.getCategoryById(id))
                .build());
    }

    @PostMapping("")
    public ResponseEntity<ResponseApi> insertCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws BadRequestException {
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .data(categoryService.insertCategory(categoryDTO))
                        .message(localizationUtil.getLocaleResolver(MessageKey.CATEGORY_INSERT_SUCCESSFULLY))
                        .build(),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseApi> updateCategory(@Valid @PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(ResponseApi.builder()
                .message(localizationUtil.getLocaleResolver(MessageKey.CATEGORY_UPDATE_SUCCESSFULLY))
                .data(categoryService.updateCategory(id, categoryDTO))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseApi> deleteCategory(@Valid @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseApi.builder()
                        .data(null)
                        .message(localizationUtil.getLocaleResolver(MessageKey.CATEGORY_DELETE_SUCCESSFULLY))
                        .build());
    }
}
