package com.learning.belightweightpaymentsystem.rmq;

import com.learning.belightweightpaymentsystem.dto.PaymentDto;
import com.learning.belightweightpaymentsystem.service.OrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final OrderServiceImpl orderService;

    @Autowired
    public RabbitMQConsumer(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consumeMessage(Message<PaymentDto> message) {
        LOGGER.info("Message received: {} with global id {}", message, message.getIdentifier());
        orderService.completeOrder(message.getMessage());
    }

}
