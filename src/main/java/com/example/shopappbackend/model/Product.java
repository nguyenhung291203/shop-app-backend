package com.example.shopappbackend.model;

import com.example.shopappbackend.model.base.BaseEntity;
import com.example.shopappbackend.model.listener.ProductListener;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(ProductListener.class)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String thumbnail;
    private String description;
    private int quantity;
    private float rating;
    private int sold;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private float price;
}
