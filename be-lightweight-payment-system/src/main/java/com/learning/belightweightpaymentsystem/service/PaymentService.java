package com.learning.belightweightpaymentsystem.service;

import com.learning.belightweightpaymentsystem.dto.PaymentDto;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;

public interface PaymentService {

    ResponseWrapper<PaymentDto> createPayment(PaymentDto paymentDto);

}
