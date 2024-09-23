package com.example.shopappbackend.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageResponse<T> {
    private List<T> contents;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
}
