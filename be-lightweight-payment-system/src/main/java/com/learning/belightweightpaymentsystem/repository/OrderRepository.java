package com.learning.belightweightpaymentsystem.repository;

import com.learning.belightweightpaymentsystem.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    List<OrderEntity> getAllByUserId(Integer userId);

    Optional<OrderEntity> findOrderByOrderId(Integer orderId);

}
