package com.learning.belightweightpaymentsystem.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PaymentDto {

    private Integer orderId;
    private Integer userId;

}
