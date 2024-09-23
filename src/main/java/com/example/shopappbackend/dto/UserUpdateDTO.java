package com.example.shopappbackend.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    @JsonProperty("fullname")
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String fullName;

    @JsonProperty("phone_number")
    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Size(max = 10, message = "Số điện thoại chỉ có tối đa 10 kí tự")
    private String phoneNumber;

    private String address;
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    private Long facebookAccountId;

    @JsonProperty("google_account_id")
    private Long googleAccountId;

    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("is_active")
    private boolean isActive;
}
