package com.example.shopappbackend.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.shopappbackend.exception.BadRequestException;

public class PageRequestUtil {
    public static Pageable getPageable(com.example.shopappbackend.model.base.PageRequest pageRequest) {
        int page = pageRequest.getPage() - 1;
        if (page < 0) throw new BadRequestException("Page < 1");
        int limit = pageRequest.getLimit();
        if (limit < 1) throw new BadRequestException("Limit < 1");
        String sortBy = pageRequest.getSortBy();
        String sortDir = pageRequest.getSortDir();
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        return PageRequest.of(page, limit, sort);
    }
}
