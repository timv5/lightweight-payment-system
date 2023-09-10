package com.learning.belightweightpaymentsystem.model;

import com.learning.belightweightpaymentsystem.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TRANSACTIONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionEntity {

    @Id
    @GeneratedValue
    private Integer transactionId;
    private Integer orderId;
    private Double transactionAmount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
    private Long externalTransactionId;

}
