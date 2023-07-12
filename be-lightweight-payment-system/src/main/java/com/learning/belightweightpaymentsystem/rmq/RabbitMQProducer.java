package com.learning.belightweightpaymentsystem.rmq;

import com.learning.belightweightpaymentsystem.model.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produceMessage(OrderEntity order) {
        Message<OrderEntity> message = new Message<>(order, new Date(), UUID.randomUUID());
        log.info("Publishing event: {}", message);
        this.rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

}
