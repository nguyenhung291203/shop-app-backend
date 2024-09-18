package com.example.shopappbackend.service.impl;


import com.example.shopappbackend.dto.CartItemDTO;
import com.example.shopappbackend.dto.OrderDTO;
import com.example.shopappbackend.dto.PageOrderDTO;
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
import com.example.shopappbackend.utils.PageRequestUtil;
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

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(localizationUtil
                        .getLocaleResolver(MessageKey.NOT_FOUND, " user id: " + id)));
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND,
                                " order id:" + id)));
    }

    private Product findProductById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() ->
                        new NotFoundException(localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND,
                                " product id: " + id)));
    }

    private OrderResponse mapOrderToOrderResponse(Order order) {
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails()
                .stream().map(orderDetail ->
                        modelMapper.map(orderDetail, OrderDetailResponse.class)).toList();
        orderResponse.setOrderDetailResponses(orderDetailResponses);
        return orderResponse;
    }

    @Override
    public OrderResponse insertOrder(OrderDTO orderDTO) {
        User user = this.findUserById(orderDTO.getUserId());

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
            Product product = this.findProductById(cartItemDTO.getProductId());
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
        Order order = this.findOrderById(id);
        User user = this.findUserById(orderDTO.getUserId());
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        return modelMapper.map(orderRepository.save(order), OrderResponse.class);
    }

    @Override
    public void deleteOrderById(Long id) {
        Order order = this.findOrderById(id);
        order.setActive(false);
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(this::mapOrderToOrderResponse).toList();
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = this.findOrderById(id);
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails()
                .stream().map(orderDetail ->
                        modelMapper.map(orderDetail, OrderDetailResponse.class)).toList();
        orderResponse.setOrderDetailResponses(orderDetailResponses);
        return orderResponse;
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        User user = this.findUserById(userId);
        return orderRepository.findAllByUser(user)
                .stream().map(this::mapOrderToOrderResponse).toList();
    }

    @Override
    public PageResponse<OrderResponse> findByKeyword(String keyword, Pageable pageable) {
        Page<Order> orders = orderRepository.findByKeyword(keyword, pageable);
        return getOrderResponsePageResponse(orders);
    }

    @Override
    public PageResponse<OrderResponse> findByUserIdAndKeyword(long userId, String keyword, Pageable pageable) {
        User user = this.findUserById(userId);
        Page<Order> orders = orderRepository.findByUserIdAndKeyword(user.getId()
                , keyword
                , pageable);
        return getOrderResponsePageResponse(orders);
    }

    @Override
    public PageResponse<OrderResponse> findAllOrders(PageOrderDTO pageOrderDTO) {
        if (pageOrderDTO.getUserId() != null) {
            this.findUserById(pageOrderDTO.getUserId());
        }
        Page<Order> orders = orderRepository.findByUserIdAndStatusAndKeywordAndOrderDateBetween(
                pageOrderDTO.getUserId(),
                pageOrderDTO.getStatus(),
                pageOrderDTO.getKeyword(),
                pageOrderDTO.getStartDate(),
                pageOrderDTO.getEndDate(),
                PageRequestUtil.getPageable(pageOrderDTO)
        );
        return getOrderResponsePageResponse(orders);
    }

    private PageResponse<OrderResponse> getOrderResponsePageResponse(Page<Order> orders) {
        List<OrderResponse> orderResponseList = orders
                .stream().map(this::mapOrderToOrderResponse).toList();
        return PageResponse.<OrderResponse>builder()
                .contents(orderResponseList)
                .numberOfElements(orders.getNumberOfElements())
                .totalPages(orders.getTotalPages())
                .totalElements(orders.getTotalElements())
                .build();
    }
}