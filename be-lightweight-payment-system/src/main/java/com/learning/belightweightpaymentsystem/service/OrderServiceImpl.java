package com.learning.belightweightpaymentsystem.service;

import com.learning.belightweightpaymentsystem.dto.Order;
import com.learning.belightweightpaymentsystem.dto.ResponseWrapper;
import com.learning.belightweightpaymentsystem.enums.OrderStatus;
import com.learning.belightweightpaymentsystem.model.OrderEntity;
import com.learning.belightweightpaymentsystem.model.ProductEntity;
import com.learning.belightweightpaymentsystem.model.ProductStockEntity;
import com.learning.belightweightpaymentsystem.model.UserBalanceEntity;
import com.learning.belightweightpaymentsystem.repository.OrderRepository;
import com.learning.belightweightpaymentsystem.repository.ProductRepository;
import com.learning.belightweightpaymentsystem.repository.ProductStockRepository;
import com.learning.belightweightpaymentsystem.repository.UserBalanceRepository;
import com.learning.belightweightpaymentsystem.utils.QRCodeGenerator;
import com.learning.belightweightpaymentsystem.utils.QRCodeUrlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    private final OrderRepository orderRepository;
    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;
    private final UserBalanceRepository userBalanceRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductStockRepository productStockRepository,
                            ProductRepository productRepository,
                            UserBalanceRepository userBalanceRepository
    ) {
        this.orderRepository = orderRepository;
        this.productStockRepository = productStockRepository;
        this.productRepository = productRepository;
        this.userBalanceRepository = userBalanceRepository;
    }

    @Transactional
    public ResponseWrapper<Order> createOrder(Order order) throws Exception {
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

        Optional<UserBalanceEntity> userBalanceEntity = userBalanceRepository.findUserBalanceEntityByUserId(order.getUserId());
        if (userBalanceEntity.isEmpty()) {
            log.error("Missing user balance for user {}", order.getUserId());
            return new ResponseWrapper<>(false);
        }

        ProductEntity product = productEntityOptional.get();
        double calculatedPrice = order.getQuantity() * product.getProductPrice();
        if (calculatedPrice > userBalanceEntity.get().getBalance()) {
            log.error("User {} balance too low", order.getUserId());
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

    private OrderEntity mapOrderEntity(Order order) {
        return OrderEntity.builder()
                .orderStatus(OrderStatus.STARTED)
                .quantity(order.getQuantity())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .qrImage(order.getQrImage())
                .build();
    }

}
