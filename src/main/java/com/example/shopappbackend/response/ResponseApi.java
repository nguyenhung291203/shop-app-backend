package com.example.shopappbackend.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseApi {
    private Object data;
    private String message;
}