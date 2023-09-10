package com.learning.belightweightpaymentsystem.model;

import com.learning.belightweightpaymentsystem.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ORDERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Lob
    @Column(name="qr_image", columnDefinition="BLOB")
    private byte[] qrImage;

}
