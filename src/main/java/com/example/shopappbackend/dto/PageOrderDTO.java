package com.example.shopappbackend.dto;

import java.time.LocalDateTime;

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
public class PageOrderDTO extends PageRequest {
    @JsonProperty("user_id")
    private Long userId;

    private String status;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;
}
