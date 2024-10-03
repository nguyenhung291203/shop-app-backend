package com.example.shopappbackend.controller;

import com.example.shopappbackend.dto.OrderDTO;
import com.example.shopappbackend.dto.PageOrderDTO;
import com.example.shopappbackend.response.ResponseApi;
import com.example.shopappbackend.service.OrderService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import com.example.shopappbackend.utils.ParamUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final LocalizationUtil localizationUtil;

    @GetMapping
    public ResponseEntity<ResponseApi> getAllOrders() {
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_GET_SUCCESSFULLY))
                        .data(orderService.getAllOrders())
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseApi> getOrderById(@Valid @PathVariable long id) {
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .data(orderService.getOrderById(id))
                        .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_GET_SUCCESSFULLY))
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<ResponseApi> insertOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .data(orderService.insertOrder(orderDTO))
                        .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_INSERT_SUCCESSFULLY))
                        .build(),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseApi> updateOrder(@Valid @PathVariable Long id, @Valid @RequestBody OrderDTO orderDTO) {
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_UPDATE_SUCCESSFULLY))
                        .data(orderService.updateOrder(id, orderDTO))
                        .build(),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseApi> deleteOrderById(@Valid @PathVariable Long id) {
        orderService.deleteOrderById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseApi.builder()
                        .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_DELETE_SUCCESSFULLY))
                        .data(null)
                        .build());
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseApi> getOrdersByOrderId(@Valid @PathVariable Long userId) {
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .data(orderService.getOrdersByUserId(userId))
                        .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_DETAIL_GET_SUCCESSFULLY))
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseApi> findByKeyword(
            @Valid @RequestParam("keyword") String keyword, @Valid @RequestParam Map<String, Object> params) {
        Pageable pageable = ParamUtil.getPageable(params);
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .data(orderService.findByKeyword(keyword.trim().toLowerCase(), pageable))
                        .message("Get success")
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/search")
    public ResponseEntity<ResponseApi> findByUserIdAndKeyword(
            @Valid @PathVariable Long userId, @Valid @RequestBody PageOrderDTO pageOrderDTO) {
        pageOrderDTO.setUserId(userId);
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .data(orderService.findAllOrders(pageOrderDTO))
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseApi> findAllOrders(@Valid @RequestBody PageOrderDTO pageOrderDTO) {
        return new ResponseEntity<>(
                ResponseApi.builder()
                        .data(orderService.findAllOrders(pageOrderDTO))
                        .build(),
                HttpStatus.OK);
    }
}
