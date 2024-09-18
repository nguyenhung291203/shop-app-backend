package com.example.shopappbackend.service.impl;

import com.example.shopappbackend.dto.UserLoginDTO;
import com.example.shopappbackend.dto.UserRegisterDTO;
import com.example.shopappbackend.dto.UserUpdateDTO;
import com.example.shopappbackend.exception.BadRequestException;
import com.example.shopappbackend.exception.DataIntegrityViolationException;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.jwt.JwtTokenProvider;
import com.example.shopappbackend.mapper.UserMapping;
import com.example.shopappbackend.model.Role;
import com.example.shopappbackend.model.User;
import com.example.shopappbackend.repository.RoleRepository;
import com.example.shopappbackend.repository.UserRepository;
import com.example.shopappbackend.response.UserResponse;
import com.example.shopappbackend.service.UserService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final LocalizationUtil localizationUtil;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public User register(UserRegisterDTO userRegisterDTO) {
        if (this.userRepository.existsUserByPhoneNumber(userRegisterDTO.getPhoneNumber()))
            throw new DataIntegrityViolationException(localizationUtil.getLocaleResolver(MessageKey.REGISTER_FAILURE_PHONE_NUMBER_ALREADY_EXIST));
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getRetypePassword()))
            throw new BadRequestException(localizationUtil.getLocaleResolver(MessageKey.REGISTER_FAILURE_INVALID_PASSWORD));
        User user = UserMapping.mapUserRegisterDTOtoUser(userRegisterDTO);
        Role role = roleRepository.findById(userRegisterDTO.getRoleId()).orElseThrow(() -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, userRegisterDTO.getRoleId())));
        user.setRole(role);
        if (userRegisterDTO.getFacebookAccountId() == null && userRegisterDTO.getGoogleAccountId() == null) {
            user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) {
        User user = this.userRepository.findUserByPhoneNumber(userLoginDTO.getPhoneNumber());
        if (user == null) throw new BadRequestException(localizationUtil.getLocaleResolver(MessageKey.LOGIN_FAILURE));
        if (user.getFacebookAccountId() == null && user.getGoogleAccountId() == null) {
            if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword()))
                throw new BadRequestException(localizationUtil.getLocaleResolver(MessageKey.LOGIN_FAILURE));
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return this.userRepository.findAll().stream().map(UserMapping::mapUserToUserResponse).toList();
    }

    @Override
    public UserResponse getUserDetail(String token) {
        if (jwtTokenProvider.isExpired(token)) throw new BadRequestException("Phiên đăng nhập đã hết hạn");
        String phoneNumber = jwtTokenProvider.getUsername(token);
        User user = this.userRepository.findUserByPhoneNumber(phoneNumber);
        if (!user.isActive()) throw new BadRequestException("Tài khoản đã bị khóa");
        return UserMapping.mapUserToUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
        if (userUpdateDTO.getPassword() != null && userUpdateDTO.getRetypePassword() != null) {
            if (!userUpdateDTO.getPassword().equals(userUpdateDTO.getRetypePassword()))
                throw new BadRequestException("Mật khẩu không chính xác");
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }
        if (userUpdateDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userUpdateDTO.getRoleId()).orElseThrow(() -> new NotFoundException("Quyền truy cập không tồn tại"));
            user.setRole(role);
        }
        User userFindByPhoneNumber = this.userRepository.findUserByPhoneNumber(userUpdateDTO.getPhoneNumber());
        if (!userFindByPhoneNumber.equals(user)) throw new BadRequestException("Số điện thoại đã được sử dụng");
        user.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        user.setFullName(userUpdateDTO.getFullName());
        user.setActive(userUpdateDTO.isActive());
        user.setAddress(userUpdateDTO.getAddress());
        return UserMapping.mapUserToUserResponse(userRepository.save(user));
    }
}
