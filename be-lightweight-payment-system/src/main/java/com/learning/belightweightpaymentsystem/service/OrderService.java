package com.learning.belightweightpaymentsystem.service;

import com.learning.belightweightpaymentsystem.dto.OrderDetailsDto;
import com.learning.belightweightpaymentsystem.dto.OrderDto;
import com.learning.belightweightpaymentsystem.dto.PaymentDto;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;

import java.util.List;

public interface OrderService {

    ResponseWrapper<OrderDto> createOrder(OrderDto order) throws Exception;

    ResponseWrapper<List<OrderDetailsDto>> getOrders(Integer userId);

    void completeOrder(PaymentDto paymentDto);

}
