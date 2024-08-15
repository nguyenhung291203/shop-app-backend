package com.example.shopappbackend.controller;

import com.example.shopappbackend.dto.OrderDetailDTO;
import com.example.shopappbackend.response.ResponseApi;
import com.example.shopappbackend.service.OrderDetailService;
import com.example.shopappbackend.utils.LocalizationUtil;
import com.example.shopappbackend.utils.MessageKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/order_details")
@Validated
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final LocalizationUtil localizationUtil;
    @GetMapping("")
    public ResponseEntity<?> getOrderDetails() {
        return ResponseEntity.ok(ResponseApi.builder()
                .data(orderDetailService.getAllOrderDetails())
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_DETAIL_GET_SUCCESSFULLY))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetailById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(ResponseApi.builder()
                .data(orderDetailService.getOrderDetailById(id))
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_DETAIL_GET_SUCCESSFULLY))
                .build());
    }

    @PostMapping("")
    public ResponseEntity<?> insertOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseApi.builder()
                        .data(orderDetailService.insertOrderDetail(orderDetailDTO))
                        .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_DETAIL_INSERT_SUCCESSFULLY))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @PathVariable Long id, @Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.ok(ResponseApi.builder()
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_DETAIL_UPDATE_SUCCESSFULLY))
                .data(orderDetailService.updateOrderDetail(id, orderDetailDTO))
                .build());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderDetailByOrderId(@Valid @PathVariable Long orderId) {
        return ResponseEntity.ok(ResponseApi.builder()
                .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_GET_SUCCESSFULLY))
                .data(orderDetailService.getOrderDetailByOrderId(orderId))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@Valid @PathVariable Long id) {
        orderDetailService.deleteOrderDetailById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseApi.builder()
                        .message(localizationUtil.getLocaleResolver(MessageKey.ORDER_DETAIL_DELETE_SUCCESSFULLY))
                        .data(null)
                        .build());
    }
}
