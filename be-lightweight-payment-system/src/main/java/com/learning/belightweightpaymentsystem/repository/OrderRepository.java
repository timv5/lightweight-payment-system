package com.learning.belightweightpaymentsystem.repository;

import com.learning.belightweightpaymentsystem.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    List<OrderEntity> getAllByUserId(Integer userId);

}
