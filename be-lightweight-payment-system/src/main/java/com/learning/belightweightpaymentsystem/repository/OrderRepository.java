package com.learning.belightweightpaymentsystem.repository;

import com.learning.belightweightpaymentsystem.enums.OrderStatus;
import com.learning.belightweightpaymentsystem.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    Optional<OrderEntity> findOrderByOrderId(Integer orderId);

    Optional<OrderEntity> findOrderEntityByOrderIdAndOrderStatus(Integer orderId, OrderStatus orderStatus);

}
