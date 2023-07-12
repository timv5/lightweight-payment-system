package com.learning.belightweightpaymentsystem.repository;

import com.learning.belightweightpaymentsystem.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {
}
