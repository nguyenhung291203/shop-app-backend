package com.example.shopappbackend.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

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
    @JsonProperty("category_id")
    private long categoryId;
    private int quantity;
    private float rating;
    private int sold;
    private float price;
    private List<String> images;
    public final static String ProductResponseRedis = "products";
}
