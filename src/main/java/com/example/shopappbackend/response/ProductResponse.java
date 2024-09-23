package com.example.shopappbackend.response;

import java.util.List;

import com.example.shopappbackend.model.Category;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse extends BaseResponse {
    private Long id;
    private String name;
    private String thumbnail;
    private String description;
    private Category category;
    private int quantity;
    private float rating;
    private int sold;
    private float price;
    private List<String> images;
    public static final String ProductResponseRedis = "products";
}
