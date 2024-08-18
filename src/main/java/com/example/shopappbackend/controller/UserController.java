package com.example.shopappbackend.controller;

import com.example.shopappbackend.dto.UserLoginDTO;
import com.example.shopappbackend.dto.UserRegisterDTO;
import com.example.shopappbackend.dto.UserUpdateDTO;
import com.example.shopappbackend.response.ResponseApi;
import com.example.shopappbackend.service.UserService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("${api.prefix}/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LocalizationUtil localizationUtil;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetail(@RequestHeader("Authorization") String jwtToken) {
        String token = jwtToken.substring(7);
        return ResponseEntity.ok(ResponseApi.builder()
                .data(userService.getUserDetail(token))
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseApi.builder()
                        .message(localizationUtil.getLocaleResolver(MessageKey.REGISTER_SUCCESS))
                        .data(userService.register(userDTO))
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(ResponseApi
                .builder()
                .data(userService.login(userLoginDTO))
                .message(localizationUtil.getLocaleResolver(MessageKey.LOGIN_SUCCESSFULLY))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable @Valid Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(ResponseApi.builder()
                .data(userService.updateUser(id, userUpdateDTO))
                .message("Cập nhật thành công")
                .build());
    }
}
