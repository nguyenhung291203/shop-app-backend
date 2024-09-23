package com.example.shopappbackend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.shopappbackend.dto.OrderDetailDTO;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.mapper.OrderDetailMapping;
import com.example.shopappbackend.model.Order;
import com.example.shopappbackend.model.OrderDetail;
import com.example.shopappbackend.model.Product;
import com.example.shopappbackend.repository.OrderDetailRepository;
import com.example.shopappbackend.repository.OrderRepository;
import com.example.shopappbackend.repository.ProductRepository;
import com.example.shopappbackend.response.OrderDetailResponse;
import com.example.shopappbackend.service.OrderDetailService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final LocalizationUtil localizationUtil;

    private OrderDetail findOrderDetailById(Long id) {
        return orderDetailRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " orderDetail id: " + id)));
    }

    private Product findProductById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, id)));
    }

    private Order findOrderById(Long id) {
        return orderRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, id)));
    }

    @Override
    public List<OrderDetailResponse> getAllOrderDetails() {
        return this.orderDetailRepository.findAll().stream()
                .map(orderDetail -> modelMapper.map(orderDetail, OrderDetailResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailResponse getOrderDetailById(Long id) {
        OrderDetail orderDetail = this.findOrderDetailById(id);
        return modelMapper.map(orderDetail, OrderDetailResponse.class);
    }

    @Override
    public OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) {
        OrderDetail orderDetail = this.findOrderDetailById(id);
        Product product = this.findProductById(orderDetailDTO.getProductId());
        Order order = orderRepository
                .findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, orderDetailDTO.getOrderId())));
        orderDetail.setOrder(order);
        orderDetail.setColor(orderDetailDTO.getColor());
        orderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        orderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        orderDetail.setPrice(orderDetailDTO.getPrice());
        orderDetail.setProduct(product);

        return modelMapper.map(orderDetailRepository.save(orderDetail), OrderDetailResponse.class);
    }

    @Override
    public OrderDetailResponse insertOrderDetail(OrderDetailDTO orderDetailDTO) {
        Product product = this.findProductById(orderDetailDTO.getProductId());
        Order order = this.findOrderById(orderDetailDTO.getOrderId());
        OrderDetail orderDetail = OrderDetailMapping.mapOrderDetailDTOToOrderDetail(orderDetailDTO, product, order);
        return modelMapper.map(orderDetailRepository.save(orderDetail), OrderDetailResponse.class);
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailByOrderId(Long orderId) {
        Order order = this.findOrderById(orderId);
        return this.orderDetailRepository.findAllByOrder(order).stream()
                .map(orderDetail -> modelMapper.map(orderDetail, OrderDetailResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrderDetailById(Long id) {
        OrderDetail orderDetail = this.findOrderDetailById(id);
        orderDetailRepository.delete(orderDetail);
    }
}
