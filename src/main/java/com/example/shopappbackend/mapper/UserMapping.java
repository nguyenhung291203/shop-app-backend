package com.example.shopappbackend.mapper;

import com.example.shopappbackend.dto.UserRegisterDTO;
import com.example.shopappbackend.model.User;
import com.example.shopappbackend.response.UserResponse;

public class UserMapping {
    private UserMapping() {
        throw new IllegalStateException("Utility class");
    }

    public static User mapUserRegisterDTOtoUser(UserRegisterDTO userRegisterDTO) {
        return User.builder()
                .dateOfBirth(userRegisterDTO.getDateOfBirth())
                .facebookAccountId(userRegisterDTO.getFacebookAccountId())
                .fullName(userRegisterDTO.getFullName())
                .googleAccountId(userRegisterDTO.getGoogleAccountId())
                .phoneNumber(userRegisterDTO.getPhoneNumber())
                .password(userRegisterDTO.getPassword())
                .address(userRegisterDTO.getAddress())
                .isActive(true)
                .build();
    }

    public static UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .roleId(user.getRole().getId())
                .dateOfBirth(user.getDateOfBirth())
                .facebookAccountId(user.getFacebookAccountId())
                .fullName(user.getFullName())
                .googleAccountId(user.getGoogleAccountId())
                .isActive(user.isActive())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .build();
    }
}
