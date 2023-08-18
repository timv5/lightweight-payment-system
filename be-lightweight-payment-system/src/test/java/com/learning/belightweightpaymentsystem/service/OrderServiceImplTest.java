package com.learning.belightweightpaymentsystem.service;

import com.learning.belightweightpaymentsystem.dto.OrderDetailsDto;
import com.learning.belightweightpaymentsystem.dto.OrderDto;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;
import com.learning.belightweightpaymentsystem.enums.OrderStatus;
import com.learning.belightweightpaymentsystem.model.OrderEntity;
import com.learning.belightweightpaymentsystem.model.ProductEntity;
import com.learning.belightweightpaymentsystem.model.ProductStockEntity;
import com.learning.belightweightpaymentsystem.repository.OrderRepository;
import com.learning.belightweightpaymentsystem.repository.ProductRepository;
import com.learning.belightweightpaymentsystem.repository.ProductStockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductStockRepository productStockRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getOrder_missing_order() {
        // input
        Integer userId = 1;
        Integer orderId = 1;

        // output
        Optional<OrderEntity> actualData = Optional.empty();
        ResponseWrapper<?> expected = new ResponseWrapper<>(false);

        when(orderRepository.findOrderByOrderId(userId)).thenReturn(actualData);
        ResponseWrapper<?> actual = orderService.getOrder(userId, orderId);

        assertEquals(expected.isSuccess(), actual.isSuccess());
        assertEquals(expected.getData(), actual.getData());
    }

    @Test
    void getOrder_missingProduct() {
        // prepare data
        Integer orderId = 1;
        Integer userId = 1;
        Integer productId = 1;
        OrderEntity data = OrderEntity.builder()
                .orderStatus(OrderStatus.STARTED)
                .userId(userId)
                .productId(productId)
                .quantity(5)
                .build();
        Optional<OrderEntity> actualData = Optional.of(data);

        ResponseWrapper<?> expected = new ResponseWrapper<>(false);

        // condition
        when(orderRepository.findOrderByOrderId(orderId)).thenReturn(actualData);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // execute
        ResponseWrapper<?> actual = orderService.getOrder(userId, orderId);

        assertEquals(expected.isSuccess(), actual.isSuccess());
        assertEquals(expected.getData(), actual.getData());
    }

    @Test
    void getOrder_success() {
        // prepare data
        Integer orderId = 1;
        Integer userId = 1;
        Integer productId = 1;
        OrderEntity data = OrderEntity.builder()
                .orderStatus(OrderStatus.STARTED)
                .userId(userId)
                .productId(productId)
                .quantity(5)
                .build();
        Optional<OrderEntity> actualData = Optional.of(data);

        ProductEntity productEntity = ProductEntity.builder()
                .productPrice(10d)
                .productName("test")
                .build();
        Optional<ProductEntity> expectedProduct = Optional.of(productEntity);

        OrderDetailsDto orderDetailsDto = OrderDetailsDto.builder()
                .productPrice(productEntity.getProductPrice())
                .productName(productEntity.getProductName())
                .orderPrice(data.getQuantity() * productEntity.getProductPrice())
                .orderStatus(data.getOrderStatus())
                .quantity(data.getQuantity())
                .orderId(data.getOrderId())
                .build();

        // expected
        ResponseWrapper<OrderDetailsDto> expected = new ResponseWrapper<>(orderDetailsDto,true);

        // condition
        when(orderRepository.findOrderByOrderId(orderId)).thenReturn(actualData);
        when(productRepository.findById(any(Integer.class))).thenReturn(expectedProduct);

        // execute
        ResponseWrapper<OrderDetailsDto> actual = orderService.getOrder(userId, orderId);

        assertEquals(expected.isSuccess(), actual.isSuccess());
        assertEquals(expected.getData().getOrderId(), actual.getData().getOrderId());
        assertEquals(expected.getData().getOrderPrice(), actual.getData().getOrderPrice());
    }

    @Test
    void getOrders_empty_success() {
        // input
        Integer orderId = 1;

        // output
        List<OrderEntity> actualData = new ArrayList<>();
        ResponseWrapper<?> expected = new ResponseWrapper<>(true);

        when(orderRepository.getAllByUserId(orderId)).thenReturn(actualData);
        ResponseWrapper<?> actual = orderService.getOrders(orderId);
        assertEquals(expected.isSuccess(), actual.isSuccess());
        assertEquals(expected.getData(), actual.getData());
    }

    @Test
    void getOrders_notEmpty_success() {
        // prepare data
        Integer orderId = 1;
        OrderEntity orderEntity = OrderEntity.builder()
                .orderStatus(OrderStatus.STARTED)
                .userId(1)
                .productId(1)
                .quantity(5)
                .build();
        List<OrderEntity> actualData = new ArrayList<>();
        actualData.add(orderEntity);

        ResponseWrapper<?> expected = new ResponseWrapper<>(true);

        when(orderRepository.getAllByUserId(orderId)).thenReturn(actualData);
        ResponseWrapper<?> actual = orderService.getOrders(orderId);
        assertEquals(expected.isSuccess(), actual.isSuccess());
        assertEquals(expected.getData(), actual.getData());
    }

    @Test
    void createOrder_missing_product() throws Exception {
        OrderDto orderDto = OrderDto.builder()
                .productId(1)
                .orderId(1)
                .build();

        Optional<ProductEntity> foundEntity = Optional.empty();
        when(productRepository.findById(orderDto.getProductId())).thenReturn(foundEntity);

        ResponseWrapper<?> expected = new ResponseWrapper<>(false);
        ResponseWrapper<?> actual = orderService.createOrder(orderDto);
        assertEquals(expected.getData(), actual.getData());
    }

    @Test
    void createOrder_missing_stock() throws Exception {
        OrderDto orderDto = OrderDto.builder()
                .productId(1)
                .orderId(1)
                .build();

        ProductEntity productEntity = ProductEntity.builder().productId(orderDto.getProductId()).build();
        Optional<ProductEntity> foundEntity = Optional.of(productEntity);
        when(productRepository.findById(orderDto.getProductId())).thenReturn(foundEntity);

        Optional<ProductStockEntity> stockOptional = Optional.empty();
        when(productStockRepository.findProductStockEntityByProductId(orderDto.getProductId())).thenReturn(stockOptional);

        ResponseWrapper<?> expected = new ResponseWrapper<>(false);
        ResponseWrapper<?> actual = orderService.createOrder(orderDto);
        assertEquals(expected.getData(), actual.getData());
    }

    @Test
    void createOrder_low_stock() throws Exception {
        OrderDto orderDto = OrderDto.builder()
                .productId(1)
                .orderId(1)
                .quantity(2)
                .build();

        ProductEntity productEntity = ProductEntity.builder().productId(orderDto.getProductId()).build();
        Optional<ProductEntity> foundEntity = Optional.of(productEntity);
        when(productRepository.findById(orderDto.getProductId())).thenReturn(foundEntity);

        ProductStockEntity productStockEntity = ProductStockEntity.builder()
                .quantity(1)
                .build();
        Optional<ProductStockEntity> stockOptional = Optional.of(productStockEntity);
        when(productStockRepository.findProductStockEntityByProductId(orderDto.getProductId())).thenReturn(stockOptional);

        ResponseWrapper<?> expected = new ResponseWrapper<>(false);
        ResponseWrapper<?> actual = orderService.createOrder(orderDto);
        assertEquals(expected.getData(), actual.getData());
    }
}