package com.example.shopappbackend.repository;

import com.example.shopappbackend.model.Order;
import com.example.shopappbackend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);

    List<Order> findAllByUser(User user);

    @Query("select o from Order o where" +
            "(:keyword is null or :keyword = '' or o.fullName like %:keyword% or o.address like %:keyword%) " +
            "or o.note like %:keyword%")
    Page<Order> findByKeyword(String keyword, Pageable pageable);

    @Query("select o from Order o where " +
            "(:userId is null or o.user.id = :userId) and " +
            "(:keyword is null or :keyword = '' or o.fullName like %:keyword% " +
            "or o.address like %:keyword% or o.note like %:keyword% or o.paymentMethod like %:keyword% or o.status like %:keyword%)")
    Page<Order> findByUserIdAndKeyword(Long userId, String keyword, Pageable pageable);
}