package com.learning.belightweightpaymentsystem.dto;

import com.learning.belightweightpaymentsystem.enums.OrderStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderDetailsDto {

    private Integer orderId;
    private Integer userId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double orderPrice;
    private OrderStatus orderStatus;

}
