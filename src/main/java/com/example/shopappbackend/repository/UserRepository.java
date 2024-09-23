package com.example.shopappbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shopappbackend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByPhoneNumber(String phoneNumber);

    boolean existsById(Long id);

    boolean existsByPhoneNumberAndPassword(String phoneNumber, String password);

    User findUserByPhoneNumber(String phoneNumber);
}
