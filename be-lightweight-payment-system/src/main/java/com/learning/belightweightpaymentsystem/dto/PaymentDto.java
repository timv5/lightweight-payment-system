package com.learning.belightweightpaymentsystem.dto;

import com.learning.belightweightpaymentsystem.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PaymentDto {

    private Integer orderId;
    private Integer userId;
    @Enumerated(EnumType.STRING)
    private OrderStatus newOrderStatus;

}
