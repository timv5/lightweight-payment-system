package com.learning.belightweightpaymentsystem.controller;

import com.learning.belightweightpaymentsystem.dto.PaymentDto;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;
import com.learning.belightweightpaymentsystem.service.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    @Autowired
    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<PaymentDto>> createPayment(@RequestBody PaymentDto paymentDto) {
        try {
            ResponseWrapper<PaymentDto> response = paymentService.createPayment(paymentDto);
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
