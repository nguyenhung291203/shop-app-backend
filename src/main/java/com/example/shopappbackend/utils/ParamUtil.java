package com.example.shopappbackend.utils;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.shopappbackend.exception.BadRequestException;

public class ParamUtil {
    public static String getSearchParam(Map<String, Object> params) {
        String keyword = (String) params.getOrDefault("search", "");
        return keyword.trim();
    }

    public static Pageable getPageable(Map<String, Object> params) {
        int page = Integer.parseInt((String) params.getOrDefault("page", "1")) - 1;
        if (page < 0) throw new BadRequestException("Page < 1");
        int limit = Integer.parseInt((String) params.getOrDefault("limit", "10"));
        if (limit < 1) throw new BadRequestException("Limit < 1");
        String sortBy = (String) params.getOrDefault("sortBy", "id");
        String sortDir = (String) params.getOrDefault("sortDir", "asc");
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        return PageRequest.of(page, limit, sort);
    }
}
