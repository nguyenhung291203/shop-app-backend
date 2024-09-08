package com.example.shopappbackend.model.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PageRequest {
    private String keyword = "";
    @Min(value = 1, message = "Trang hiện tại không được nhỏ hơn 1")
    private int page = 1;
    private int limit = 10;
    @JsonProperty("sort_by")
    private String sortBy = "id";
    @JsonProperty("sort_dir")
    private String sortDir = "asc";

    @Override
    public String toString() {
        return "keyword:" + keyword + '-'
                + "page:" + page + '-'
                + "limit:" + limit + '-'
                + "sort_by:" + sortBy + '-'
                + "sort_dir:" + sortDir + '-';
    }
}