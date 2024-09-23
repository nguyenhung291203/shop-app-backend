package com.example.shopappbackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shopappbackend.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    @Query("SELECT p FROM Product p WHERE "
            + "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR (LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))"
            + "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')))  OR :keyword IS NULL)"
            + "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> findByKeywordAndCategory(
            @Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
}
