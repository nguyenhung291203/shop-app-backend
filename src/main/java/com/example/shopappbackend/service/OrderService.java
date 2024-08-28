package com.example.shopappbackend.service;

import com.example.shopappbackend.dto.OrderDTO;
import com.example.shopappbackend.model.Order;
import com.example.shopappbackend.response.OrderResponse;
import com.example.shopappbackend.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse insertOrder(OrderDTO orderDTO);

    OrderResponse updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrderById(Long id);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getOrdersByUserId(Long userId);
    PageResponse<OrderResponse> findByKeyword(String keyword, Pageable pageable);
}
