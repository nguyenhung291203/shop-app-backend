package com.example.shopappbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.example.shopappbackend.dto.UserLoginDTO;
import com.example.shopappbackend.dto.UserRegisterDTO;
import com.example.shopappbackend.exception.BadRequestException;
import com.example.shopappbackend.exception.DataIntegrityViolationException;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.jwt.JwtTokenProvider;
import com.example.shopappbackend.model.Role;
import com.example.shopappbackend.model.User;
import com.example.shopappbackend.repository.RoleRepository;
import com.example.shopappbackend.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    UserRegisterDTO userRegisterDTO;
    User userResponse;
    UserLoginDTO userLoginDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // Mở các mock annotations

        userRegisterDTO = UserRegisterDTO.builder()
                .fullName("Nguyen Van Hung")
                .address("Tân Thành - Vụ Bản - Nam Định")
                .phoneNumber("0869885517")
                .dateOfBirth(new Date())
                .password("123456")
                .retypePassword("123456")
                .roleId(1L)
                .build();

        userResponse = User.builder()
                .fullName("Nguyen Van Hung")
                .address("Tân Thành - Vụ Bản - Nam Định")
                .phoneNumber("0869885517")
                .role(new Role(1L, "ROLE_USER"))
                .id(1L)
                .facebookAccountId(null)
                .googleAccountId(null)
                .build();

        userLoginDTO = UserLoginDTO.builder()
                .phoneNumber("0869885512")
                .password("123456")
                .build();
    }

    @Test
    void register_validRequest_success() {
        when(userRepository.existsUserByPhoneNumber(anyString())).thenReturn(false);

        when(roleRepository.findById(userRegisterDTO.getRoleId()))
                .thenReturn(java.util.Optional.of(new Role(1L, "ROLE_USER")));

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(userResponse);

        var response = userService.register(userRegisterDTO);

        Assertions.assertThat(response.getId()).isEqualTo(1L);
        Assertions.assertThat(response.getFullName()).isEqualTo(userRegisterDTO.getFullName());
        Assertions.assertThat(response.getAddress()).isEqualTo(userRegisterDTO.getAddress());
    }

    @Test
    void register_phoneNumberExist_fail() {
        when(userRepository.existsUserByPhoneNumber(anyString())).thenReturn(true);
        var ex = assertThrows(DataIntegrityViolationException.class, () -> userService.register(userRegisterDTO));
        Assertions.assertThat(ex.getMessage()).isEqualTo("Phone number already exists");
    }

    @Test
    void register_validPassword_fail() {
        userRegisterDTO.setPassword("123456890");
        var ex = assertThrows(BadRequestException.class, () -> userService.register(userRegisterDTO));
        Assertions.assertThat(ex.getMessage()).isEqualTo("Invalid password");
    }

    @Test
    void register_notFoundRole_fail() {
        userRegisterDTO.setRoleId(10L);
        var ex = assertThrows(NotFoundException.class, () -> userService.register(userRegisterDTO));
        Assertions.assertThat(ex.getMessage()).isEqualTo("Can not found");
    }

    @Test
    void login_validRequest_success() {
        when(userRepository.existsUserByPhoneNumber(anyString())).thenReturn(true);
        when(this.userRepository.findUserByPhoneNumber(anyString())).thenReturn(userResponse);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");
        var token = userService.login(userLoginDTO);
        assertEquals(token, "jwt-token");
    }

    @Test
    void login_whenPhoneNumberIsEmpty_throwBadRequestExceptionLOGIN_FAILURE() {
        when(this.userRepository.findUserByPhoneNumber(anyString())).thenReturn(null);
        var response = assertThrows(BadRequestException.class, () -> userService.login(userLoginDTO));
        assertEquals(response.getMessage(), "Account or password is incorrect");
    }

    @Test
    void login_whenPasswordIsIncorrect_throwBadRequestExceptionLOGIN_FAILURE() {
        when(userRepository.existsUserByPhoneNumber(anyString())).thenReturn(false);
        when(this.userRepository.findUserByPhoneNumber(anyString())).thenReturn(userResponse);
        var response = assertThrows(BadRequestException.class, () -> userService.login(userLoginDTO));
        assertEquals(response.getMessage(), "Account or password is incorrect");
    }
}
