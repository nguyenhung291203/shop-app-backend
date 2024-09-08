package com.example.shopappbackend.dto;

import com.example.shopappbackend.model.base.PageRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageProductDTO extends PageRequest {
    @JsonProperty("category_id")
    private Long categoryId;
}
