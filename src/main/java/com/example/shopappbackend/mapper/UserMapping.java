package com.example.shopappbackend.mapper;

import com.example.shopappbackend.dto.UserRegisterDTO;
import com.example.shopappbackend.model.User;
import com.example.shopappbackend.response.UserResponse;

public class UserMapping {
    public static User mapUserRegisterDTOtoUser(UserRegisterDTO userRegisterDTO) {
        return User.builder()
                .dateOfBirth(userRegisterDTO.getDateOfBirth())
                .facebookAccountId(userRegisterDTO.getFacebookAccountId())
                .fullName(userRegisterDTO.getFullName())
                .googleAccountId(userRegisterDTO.getGoogleAccountId())
                .phoneNumber(userRegisterDTO.getPhoneNumber())
                .password(userRegisterDTO.getPassword())
                .isActive(true)
                .build();
    }

    public static UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .roleId(user.getRole().getId())
                .dateOfBirth(user.getDateOfBirth())
                .facebookAccountId(user.getFacebookAccountId())
                .fullName(user.getFullName())
                .googleAccountId(user.getGoogleAccountId())
                .isActive(user.isActive())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
