package com.learning.belightweightpaymentsystem.controller;

import com.learning.belightweightpaymentsystem.dto.OrderDetailsDto;
import com.learning.belightweightpaymentsystem.dto.OrderDto;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;
import com.learning.belightweightpaymentsystem.service.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<OrderDto>> creteOrder(@RequestBody OrderDto order) {
        try {
            ResponseWrapper<OrderDto> response = orderService.createOrder(order);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseWrapper<List<OrderDetailsDto>>> getOrders(@RequestParam Integer userId) {
        try {
            ResponseWrapper<List<OrderDetailsDto>> response = orderService.getOrders(userId);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/details")
    public ResponseEntity<ResponseWrapper<OrderDetailsDto>> getOrder(@RequestParam Integer userId, @RequestParam Integer orderId) {
        try {
            ResponseWrapper<OrderDetailsDto> response = orderService.getOrder(userId, orderId);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
