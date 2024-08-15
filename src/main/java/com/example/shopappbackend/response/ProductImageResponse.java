package com.example.shopappbackend.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponse {
    private long id;
    private String imageUrl;
    private Long productId;
}
