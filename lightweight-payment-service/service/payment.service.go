package service

import (
	"lightweight-payment-service/configs"
	"lightweight-payment-service/dto/request"
	"lightweight-payment-service/dto/response"
	rmq2 "lightweight-payment-service/dto/rmq"
	"lightweight-payment-service/dto/stripe"
	"lightweight-payment-service/model"
	"lightweight-payment-service/repository"
	"lightweight-payment-service/rmq"
)

type PaymentServiceInterface interface {
	StartPayment(paymentRequest *request.PaymentRequest) (response.PaymentResponse, error)
}

type PaymentService struct {
	conf                   *configs.Config
	transactionRepository  *repository.TransactionRepository
	orderRepository        *repository.OrderRepository
	productStockRepository *repository.ProductStockRepository
	stripeService          *StripeService
	productRepository      *repository.ProductRepository
}

func NewPaymentService(config *configs.Config, transactionRepository *repository.TransactionRepository,
	orderRepository *repository.OrderRepository, productStockRepository *repository.ProductStockRepository,
	productRepository *repository.ProductRepository, stripeService *StripeService) *PaymentService {
	return &PaymentService{
		conf:                   config,
		transactionRepository:  transactionRepository,
		orderRepository:        orderRepository,
		productStockRepository: productStockRepository,
		productRepository:      productRepository,
		stripeService:          stripeService,
	}
}

func (paymentService *PaymentService) StartPayment(paymentRequest *request.PaymentRequest) (response.PaymentResponse, error) {
	// check order - validation
	var orderEntity model.Order
	orderEntity, err := paymentService.orderRepository.GetOrder(paymentRequest.OrderID)
	if err != nil {
		return response.PaymentResponse{Status: false, Message: "Missing order"}, err
	}

	// check product validation
	var productEntity model.Product
	productEntity, err = paymentService.productRepository.GetProduct(orderEntity.ProductID)
	if err != nil {
		return response.PaymentResponse{Status: false, Message: "Not found"}, err
	} else if (productEntity == model.Product{}) {
		return response.PaymentResponse{Status: false, Message: "Missing product"}, err
	}

	// check product & stock
	var productStockEntity model.ProductStock
	productStockEntity, err = paymentService.productStockRepository.GetProductStock(orderEntity.ProductID)
	if err != nil {
		return response.PaymentResponse{Status: false, Message: "Not found"}, err
	} else if (productStockEntity == model.ProductStock{}) {
		return response.PaymentResponse{Status: false, Message: "Missing stock"}, err
	}

	if orderEntity.Quantity > productStockEntity.Quantity {
		return response.PaymentResponse{Status: false, Message: "Not enough stock"}, nil
	}

	// start stripe payment
	var stripeResponse stripe.StripeResponse
	stripeResponse = paymentService.stripeService.StartStripePayment(
		orderEntity.Quantity,
		productEntity.ProductName,
		productEntity.ProductPrice,
	)
	if stripeResponse.Status != "success" {
		return response.PaymentResponse{Status: false, Message: "error on stripe side"}, nil
	} else {
		// publish message
		rmqProducer := rmq.RMQProducer{
			ExchangeKey:      paymentService.conf.RMQExchangeKey,
			QueueName:        paymentService.conf.RMQQueueName,
			ConnectionString: paymentService.conf.RMQUrl,
		}

		var savedMessage = rmq2.PaymentDto{OrderId: paymentRequest.OrderID, UserId: orderEntity.UserID, NewOrderStatus: "COMPLETED"}
		rmqProducer.ProduceMessage(&savedMessage)
	}

	return response.PaymentResponse{Status: true, Message: "success"}, nil
}
