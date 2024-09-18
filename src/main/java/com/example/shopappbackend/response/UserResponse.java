package com.example.shopappbackend.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("is_active")
    private boolean isActive;
    @JsonProperty("date_of_birth")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;
    @JsonProperty("facebook_account_id")
    private Long facebookAccountId;
    @JsonProperty("google_account_id")
    private Long googleAccountId;
    @JsonProperty("role_id")
    private Long roleId;
    private String address;
}
