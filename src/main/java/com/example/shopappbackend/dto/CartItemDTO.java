package com.example.shopappbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    @JsonProperty("product_id")
    @NotBlank(message = "ID sản phẩm không được để trống")
    private Long productId;

    @Size(min = 1, message = "Số lượng không hợp lệ")
    private Integer quantity;
}
