package com.example.shopappbackend.service.impl;


import com.example.shopappbackend.dto.CartItemDTO;
import com.example.shopappbackend.dto.OrderDTO;
import com.example.shopappbackend.exception.BadRequestException;
import com.example.shopappbackend.exception.NotFoundException;
import com.example.shopappbackend.model.*;
import com.example.shopappbackend.repository.OrderDetailRepository;
import com.example.shopappbackend.repository.OrderRepository;
import com.example.shopappbackend.repository.ProductRepository;
import com.example.shopappbackend.repository.UserRepository;
import com.example.shopappbackend.response.OrderDetailResponse;
import com.example.shopappbackend.response.OrderResponse;
import com.example.shopappbackend.response.PageResponse;
import com.example.shopappbackend.service.OrderService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final LocalizationUtil localizationUtil;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public OrderResponse insertOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " user id: " + orderDTO.getUserId())));

        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = modelMapper.map(orderDTO, Order.class);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(new Date());

        Date shippingDate = orderDTO.getShippingDate();
        if (shippingDate == null) {
            shippingDate = new Date();
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            Product product = productRepository
                    .findById(cartItemDTO.getProductId())
                    .orElseThrow(() ->
                            new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " product id: " + cartItemDTO.getProductId())));
            if (product.getQuantity() < cartItemDTO.getQuantity())
                throw new BadRequestException("Số lượng sản phẩm trong kho không đủ");
            product.setQuantity(product.getQuantity() - cartItemDTO.getQuantity());
            product.setSold(product.getSold() + cartItemDTO.getQuantity());
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(cartItemDTO.getQuantity());
            orderDetail.setPrice(product.getPrice());
            orderDetail.setTotalMoney(product.getPrice() * cartItemDTO.getQuantity());
            orderDetails.add(orderDetail);
            products.add(product);
        }
        orderRepository.save(order);
        productRepository.saveAll(products);
        orderDetailRepository.saveAll(orderDetails);
        return modelMapper.map(order, OrderResponse.class);
    }


    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " order id:" + id)));
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " user id: " + orderDTO.getUserId())));
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, order);
        order.setUser(user);

        return modelMapper.map(orderRepository.save(order), OrderResponse.class);
    }

    @Override
    public void deleteOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " order id:" + id)));
        order.setActive(false);
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(order -> {
                    OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
                    List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails()
                            .stream().map(orderDetail ->
                                    modelMapper.map(orderDetail, OrderDetailResponse.class)).toList();
                    orderResponse.setOrderDetailResponses(orderDetailResponses);
                    return orderResponse;
                }).toList();

    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " order id:" + id)));
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails()
                .stream().map(orderDetail ->
                        modelMapper.map(orderDetail, OrderDetailResponse.class)).toList();
        orderResponse.setOrderDetailResponses(orderDetailResponses);
        return orderResponse;
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " user id: " + userId)));

        return orderRepository.findAllByUser(user)
                .stream().map(order -> {
                    OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
                    List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails()
                            .stream().map(orderDetail ->
                                    modelMapper.map(orderDetail, OrderDetailResponse.class)).toList();
                    orderResponse.setOrderDetailResponses(orderDetailResponses);
                    return orderResponse;
                }).toList();
    }

    @Override
    public PageResponse<OrderResponse> findByKeyword(String keyword, Pageable pageable) {
        Page<Order> orders = orderRepository.findByKeyword(keyword, pageable);
        List<OrderResponse> orderResponseList = orders
                .stream().map(order -> {
                    OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
                    List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails()
                            .stream().map(orderDetail ->
                                    modelMapper.map(orderDetail, OrderDetailResponse.class)).toList();
                    orderResponse.setOrderDetailResponses(orderDetailResponses);
                    return orderResponse;
                }).toList();
        return PageResponse.<OrderResponse>builder()
                .contents(orderResponseList)
                .numberOfElements(orders.getNumberOfElements())
                .totalPages(orders.getTotalPages())
                .totalElements(orders.getTotalElements())
                .build();
    }
}