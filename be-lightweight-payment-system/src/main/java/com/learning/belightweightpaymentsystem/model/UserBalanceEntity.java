package com.learning.belightweightpaymentsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_BALANCES")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBalanceEntity {

    @Id
    @GeneratedValue
    private Integer userBalanceId;
    private Integer userId;
    private Double balance;

}
