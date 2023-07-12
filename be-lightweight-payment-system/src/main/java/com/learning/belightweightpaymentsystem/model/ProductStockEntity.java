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
@Table(name = "PRODUCTS_STOCK")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductStockEntity {

    @Id
    @GeneratedValue
    private Integer productStockId;
    private Integer productId;
    private Integer quantity;

}
