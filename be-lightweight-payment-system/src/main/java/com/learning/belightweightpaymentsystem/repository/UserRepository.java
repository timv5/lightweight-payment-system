package com.learning.belightweightpaymentsystem.repository;

import com.learning.belightweightpaymentsystem.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
