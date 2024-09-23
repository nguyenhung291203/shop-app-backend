package com.example.shopappbackend.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.example.shopappbackend.dto.OrderDTO;
import com.example.shopappbackend.dto.PageOrderDTO;
import com.example.shopappbackend.response.OrderResponse;
import com.example.shopappbackend.response.PageResponse;

public interface OrderService {
    OrderResponse insertOrder(OrderDTO orderDTO);

    OrderResponse updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrderById(Long id);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getOrdersByUserId(Long userId);

    PageResponse<OrderResponse> findByKeyword(String keyword, Pageable pageable);

    PageResponse<OrderResponse> findByUserIdAndKeyword(long userId, String keyword, Pageable pageable);

    PageResponse<OrderResponse> findAllOrders(PageOrderDTO pageOrderDTO);
}
