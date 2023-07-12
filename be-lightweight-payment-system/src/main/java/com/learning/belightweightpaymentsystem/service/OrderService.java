package com.learning.belightweightpaymentsystem.service;

import com.learning.belightweightpaymentsystem.dto.Order;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;

public interface OrderService {

    ResponseWrapper<Order> createOrder(Order order) throws Exception;

}
