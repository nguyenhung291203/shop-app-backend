package com.example.shopappbackend.controller;

import com.example.shopappbackend.dto.OrderDTO;
import com.example.shopappbackend.response.ResponseApi;
import com.example.shopappbackend.service.OrderService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final LocalizationUtil localizationUtil;
    @GetMapping
    public ResponseEntity<?> getAllOrderById() {
        return new ResponseEntity<>(ResponseApi.builder()
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_GET_SUCCESSFULLY))
                .data(orderService.getAllOrders())
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@Valid @PathVariable long id) {
        return new ResponseEntity<>(ResponseApi.builder()
                .data(orderService.getOrderById(id))
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_GET_SUCCESSFULLY))
                .build(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> insertOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return new ResponseEntity<>(ResponseApi.builder()
                .data(orderService.insertOrder(orderDTO))
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_INSERT_SUCCESSFULLY))
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@Valid @PathVariable Long id, @Valid @RequestBody OrderDTO orderDTO) {
        return new ResponseEntity<>(ResponseApi.builder()
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_UPDATE_SUCCESSFULLY))
                .data(orderService.updateOrder(id, orderDTO))
                .build(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderById(@Valid @PathVariable Long id) {
        orderService.deleteOrderById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseApi.builder()
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_DELETE_SUCCESSFULLY))
                .data(null)
                .build());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getOrdersByOrderId(@Valid @PathVariable Long userId) {
        return new ResponseEntity<>(ResponseApi.builder()
                .data(orderService.getOrdersByUserId(userId))
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_DETAIL_GET_SUCCESSFULLY))
                .build(), HttpStatus.OK);
    }
}
