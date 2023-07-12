package com.learning.belightweightpaymentsystem.repository;

import com.learning.belightweightpaymentsystem.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
}
