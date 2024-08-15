package com.example.shopappbackend.service.impl;

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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final LocalizationUtil localizationUtil;

    @Override
    public List<OrderDetailResponse> getAllOrderDetails() {
        return this.orderDetailRepository.findAll()
                .stream().map(orderDetail -> modelMapper.map(orderDetail, OrderDetailResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailResponse getOrderDetailById(Long id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " orderDetail id: " + id)));

        return modelMapper.map(orderDetail, OrderDetailResponse.class);
    }

    @Override
    public OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " orderDetail id: " + id)));
        Product product = productRepository.findById(orderDetailDTO
                .getProductId()).orElseThrow(() ->
                new NotFoundException(localizationUtil
                        .getLocaleResolver(MessageKey.NOT_FOUND
                                , orderDetailDTO.getProductId())));
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() ->
                        new NotFoundException(localizationUtil
                                .getLocaleResolver(MessageKey.NOT_FOUND
                                        , orderDetailDTO.getOrderId())));
        orderDetail.setOrder(order);
        orderDetail.setColor(orderDetailDTO.getColor());
        orderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        orderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        orderDetail.setPrice(orderDetailDTO.getPrice());
        orderDetail.setProduct(product);

        return modelMapper.map(orderDetailRepository.save(orderDetail)
                , OrderDetailResponse.class);
    }

    @Override
    public OrderDetailResponse insertOrderDetail(OrderDetailDTO orderDetailDTO) {
        Product product = productRepository.findById(orderDetailDTO
                .getProductId()).orElseThrow(() ->
                new NotFoundException(localizationUtil
                        .getLocaleResolver(MessageKey.NOT_FOUND
                                , orderDetailDTO.getProductId())));
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() ->
                        new NotFoundException(localizationUtil
                                .getLocaleResolver(MessageKey.NOT_FOUND
                                        , orderDetailDTO.getOrderId())));
        OrderDetail orderDetail = OrderDetailMapping.mapOrderDetailDTOToOrderDetail(orderDetailDTO, product, order);
        return modelMapper.map(orderDetailRepository.save(orderDetail), OrderDetailResponse.class);

    }

    @Override
    public List<OrderDetailResponse> getOrderDetailByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new NotFoundException(
                                localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " order id: " + orderId)));


        return this.orderDetailRepository.findAllByOrder(order)
                .stream().map(orderDetail -> modelMapper.map(orderDetail, OrderDetailResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrderDetailById(Long id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        localizationUtil.getLocaleResolver(MessageKey.NOT_FOUND, " orderDetail id: " + id)));
        orderDetailRepository.delete(orderDetail);
    }
}
