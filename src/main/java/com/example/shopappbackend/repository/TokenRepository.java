package com.example.shopappbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shopappbackend.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {}
