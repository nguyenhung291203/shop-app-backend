package com.example.shopappbackend.model.base;

import jakarta.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PageRequest {
    @Builder.Default
    private String keyword = "";

    @Min(value = 1, message = "Trang hiện tại không được nhỏ hơn 1")
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int limit = 10;

    @JsonProperty("sort_by")
    @Builder.Default
    private String sortBy = "id";

    @JsonProperty("sort_dir")
    @Builder.Default
    private String sortDir = "asc";

    @Override
    public String toString() {
        return "keyword:" + keyword.trim() + '-'
                + "page:" + page + '-'
                + "limit:" + limit + '-'
                + "sort_by:" + sortBy + '-'
                + "sort_dir:" + sortDir + '-';
    }
}
