package com.learning.belightweightpaymentsystem.repository;

import com.learning.belightweightpaymentsystem.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
}
