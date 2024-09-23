package com.example.shopappbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserLoginDTO {
    @JsonProperty("phone_number")
    @NotBlank(message = "Số điện thoại là bắt buộc")
    @NotEmpty(message = "Số điện thoại là bắt buộc")
    private String phoneNumber;

    @NotBlank(message = "Mật khẩu là bất buộc")
    private String password;
}
