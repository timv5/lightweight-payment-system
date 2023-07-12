package com.learning.belightweightpaymentsystem.repository;

import com.learning.belightweightpaymentsystem.model.ProductStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStockEntity, Integer> {

    Optional<ProductStockEntity> findProductStockEntityByProductId(Integer productId);

}
