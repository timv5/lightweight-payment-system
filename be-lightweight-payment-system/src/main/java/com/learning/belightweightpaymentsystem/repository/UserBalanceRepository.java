package com.learning.belightweightpaymentsystem.repository;

import com.learning.belightweightpaymentsystem.model.UserBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBalanceRepository extends JpaRepository<UserBalanceEntity, Integer> {

    Optional<UserBalanceEntity> findUserBalanceEntityByUserId(Integer userId);

}
