package com.learning.belightweightpaymentsystem.service;

import com.learning.belightweightpaymentsystem.dto.OrderDetailsDto;
import com.learning.belightweightpaymentsystem.dto.OrderDto;
import com.learning.belightweightpaymentsystem.dto.PaymentDto;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;
import com.learning.belightweightpaymentsystem.enums.OrderStatus;
import com.learning.belightweightpaymentsystem.model.OrderEntity;
import com.learning.belightweightpaymentsystem.model.ProductEntity;
import com.learning.belightweightpaymentsystem.model.ProductStockEntity;
import com.learning.belightweightpaymentsystem.repository.OrderRepository;
import com.learning.belightweightpaymentsystem.repository.ProductRepository;
import com.learning.belightweightpaymentsystem.repository.ProductStockRepository;
import com.learning.belightweightpaymentsystem.utils.QRCodeGenerator;
import com.learning.belightweightpaymentsystem.utils.QRCodeUrlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    private final OrderRepository orderRepository;
    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductStockRepository productStockRepository,
                            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.productStockRepository = productStockRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void completeOrder(PaymentDto paymentDto) {
        if (paymentDto == null || paymentDto.getOrderId() == null || paymentDto.getUserId() == null || paymentDto.getNewOrderStatus() == null) {
            log.error("Missing message data");
            return;
        }

        Optional<OrderEntity> orderEntity = orderRepository.findById(paymentDto.getOrderId())
                .filter(order -> !order.getOrderStatus().equals(OrderStatus.COMPLETED));
        orderEntity.ifPresent(entity -> {
            entity.setOrderStatus(paymentDto.getNewOrderStatus());
            orderRepository.save(entity);
            log.info("Completing order {} with status", entity.getOrderStatus());
        });
    }

    public ResponseWrapper<List<OrderDetailsDto>> getOrders(Integer userId) {
        List<OrderEntity> orders = orderRepository.getAllByUserId(userId);
        if (orders.isEmpty()) {
            return new ResponseWrapper<>(true);
        }

        List<OrderDetailsDto> details = new ArrayList<>();
        for (OrderEntity order: orders) {
            Optional<ProductEntity> productEntity = productRepository.findById(order.getProductId());
            if (productEntity.isEmpty()) {
                continue;
            }
            OrderDetailsDto orderDetailsDto = OrderDetailsDto.builder()
                    .productPrice(productEntity.get().getProductPrice())
                    .productName(productEntity.get().getProductName())
                    .orderStatus(order.getOrderStatus())
                    .quantity(order.getQuantity())
                    .userId(userId)
                    .orderPrice(order.getQuantity() * productEntity.get().getProductPrice())
                    .orderId(order.getOrderId())
                    .build();
            details.add(orderDetailsDto);
        }

        return new ResponseWrapper<>(details, true);
    }

    @Override
    public ResponseWrapper<OrderDetailsDto> getOrder(Integer userId, Integer orderId) {
        Optional<OrderEntity> order = orderRepository.findOrderByOrderId(orderId);
        if (order.isEmpty()) {
            log.error("Missing order {}", orderId);
            return new ResponseWrapper<>(false);
        }

        Optional<ProductEntity> productEntity = productRepository.findById(order.get().getProductId());
        if (productEntity.isEmpty()) {
            log.error("Missing product {} for order {}", order.get().getProductId(), order.get().getOrderId());
            return new ResponseWrapper<>(false);
        }

        OrderDetailsDto orderDetailsDto = OrderDetailsDto.builder()
                .productPrice(productEntity.get().getProductPrice())
                .productName(productEntity.get().getProductName())
                .orderStatus(order.get().getOrderStatus())
                .quantity(order.get().getQuantity())
                .userId(userId)
                .orderPrice(order.get().getQuantity() * productEntity.get().getProductPrice())
                .orderId(order.get().getOrderId())
                .build();

        return new ResponseWrapper<>(orderDetailsDto, true);
    }

    @Transactional
    public ResponseWrapper<OrderDto> createOrder(OrderDto order) throws Exception {
        Optional<ProductEntity> productEntityOptional = productRepository.findById(order.getProductId());
        if (productEntityOptional.isEmpty()) {
            log.error("Cannot find product with id {}", order.getProductId());
            return new ResponseWrapper<>(false);
        }

        Optional<ProductStockEntity> stockOptional = productStockRepository.findProductStockEntityByProductId(order.getProductId());
        if (stockOptional.isEmpty()) {
            log.error("Missing stock for product {}", order.getProductId());
            return new ResponseWrapper<>(false);
        }

        ProductStockEntity stock = stockOptional.get();
        if (stock.getQuantity() < order.getQuantity()) {
            log.error("Stock too low for product {}", order.getProductId());
            return new ResponseWrapper<>(false);
        }

        OrderEntity orderEntity = orderRepository.save(mapOrderEntity(order));
        order.setOrderStatus(OrderStatus.STARTED);
        order.setOrderId(orderEntity.getOrderId());

        // create QR code and save it to DB
        byte[] qrCode = QRCodeGenerator.generateQRCode(
                QRCodeUrlGenerator.generateUrl(frontendBaseUrl, order.getUserId(), orderEntity.getOrderId())
        );
        orderEntity.setQrImage(qrCode);
        orderRepository.save(orderEntity);

        order.setQrImage(qrCode);
        return new ResponseWrapper<>(order, true);
    }

    private OrderEntity mapOrderEntity(OrderDto order) {
        return OrderEntity.builder()
                .orderStatus(OrderStatus.STARTED)
                .quantity(order.getQuantity())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .qrImage(order.getQrImage())
                .build();
    }

}
