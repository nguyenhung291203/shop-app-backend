package com.example.shopappbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="order_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="order_id")
    @JsonBackReference
    private Order order;
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;
    private float price;
    @Column(name="number_of_products")
    private int numberOfProducts;
    @Column(name="total_money")
    private float totalMoney;
    @Column(length = 20)
    private String color;
}
