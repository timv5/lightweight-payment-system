package com.learning.belightweightpaymentsystem.service;

import com.learning.belightweightpaymentsystem.dto.PaymentDto;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;
import com.learning.belightweightpaymentsystem.enums.OrderStatus;
import com.learning.belightweightpaymentsystem.model.OrderEntity;
import com.learning.belightweightpaymentsystem.model.ProductEntity;
import com.learning.belightweightpaymentsystem.model.ProductStockEntity;
import com.learning.belightweightpaymentsystem.repository.OrderRepository;
import com.learning.belightweightpaymentsystem.repository.ProductRepository;
import com.learning.belightweightpaymentsystem.repository.ProductStockRepository;
import com.learning.belightweightpaymentsystem.rmq.RabbitMQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductStockRepository productStockRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Autowired
    public PaymentServiceImpl(ProductRepository productRepository,
                              OrderRepository orderRepository,
                              ProductStockRepository productStockRepository,
                              RabbitMQProducer rabbitMQProducer
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productStockRepository = productStockRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Transactional
    @Override
    public ResponseWrapper<PaymentDto> createPayment(PaymentDto paymentDto) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(paymentDto.getOrderId());
        if (orderEntity.isEmpty()) {
            log.error("Missing order {}", paymentDto.getOrderId());
            return new ResponseWrapper<>(false);
        }

        Optional<ProductStockEntity> stockOptional = productStockRepository.findProductStockEntityByProductId(orderEntity.get().getProductId());
        if (stockOptional.isEmpty()) {
            log.error("Missing stock for product {}", orderEntity.get().getProductId());
            return new ResponseWrapper<>(false);
        }

        if (orderEntity.get().getQuantity() > stockOptional.get().getQuantity()) {
            log.error("Stock too low for order {}", orderEntity.get().getOrderId());
            return new ResponseWrapper<>(false);
        }

        // update status
        orderEntity.get().setOrderStatus(OrderStatus.WAITING_PAYMENT);
        orderRepository.save(orderEntity.get());

        // send message to payment service with rmq
        rabbitMQProducer.produceMessage(orderEntity.get());

        return new ResponseWrapper<>(paymentDto, true);
    }
}
