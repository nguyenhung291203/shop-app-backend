package com.example.shopappbackend.service;

import java.util.List;

import com.example.shopappbackend.dto.OrderDetailDTO;
import com.example.shopappbackend.response.OrderDetailResponse;

public interface OrderDetailService {
    List<OrderDetailResponse> getAllOrderDetails();

    OrderDetailResponse getOrderDetailById(Long id);

    OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO);

    OrderDetailResponse insertOrderDetail(OrderDetailDTO orderDetailDTO);

    List<OrderDetailResponse> getOrderDetailByOrderId(Long orderId);

    void deleteOrderDetailById(Long id);
}
