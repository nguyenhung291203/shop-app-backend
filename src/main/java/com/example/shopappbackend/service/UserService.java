package com.example.shopappbackend.service;

import com.example.shopappbackend.dto.UserLoginDTO;
import com.example.shopappbackend.dto.UserRegisterDTO;
import com.example.shopappbackend.dto.UserUpdateDTO;
import com.example.shopappbackend.model.User;
import com.example.shopappbackend.response.UserResponse;

import java.util.List;

public interface UserService {
    User register(UserRegisterDTO userRegisterDTO);
    String login(UserLoginDTO userLoginDTO);
    List<UserResponse> getAllUsers();
    UserResponse getUserDetail(String token);
    UserResponse updateUser(Long id, UserUpdateDTO userUpdateDTO);
}
