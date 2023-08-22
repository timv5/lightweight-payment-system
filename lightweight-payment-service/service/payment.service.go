package service

import (
	"lightweight-payment-service/configs"
	"lightweight-payment-service/dto/enums"
	"lightweight-payment-service/dto/request"
	"lightweight-payment-service/dto/response"
	rmq2 "lightweight-payment-service/dto/rmq"
	"lightweight-payment-service/dto/stripe"
	"lightweight-payment-service/model"
	"lightweight-payment-service/repository"
	"lightweight-payment-service/rmq"
	"log"
)

type PaymentServiceInterface interface {
	StartPayment(paymentRequest *request.PaymentRequest) (response.PaymentResponse, error)
	CompletePayment(stripeWebhookRequest *request.WebhookRequest)
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

func (paymentService *PaymentService) CompletePayment(stripeWebhookRequest *request.WebhookRequest) {
	newStatus := enums.STARTED
	switch stripeWebhookRequest.Type {
	case "payment_intent.succeeded":
		newStatus = enums.COMPLETED
	case "payment_intent.canceled":
		newStatus = enums.FAILED
	case "payment_method.attached":
		newStatus = enums.STARTED
	default:
		log.Println("Webhook: unprocessed event type: " + stripeWebhookRequest.Type)
		return
	}

	var transactionEntity model.Transaction
	transactionEntity = paymentService.transactionRepository.GetTransactionByExternalId(stripeWebhookRequest.ID)
	if (transactionEntity == model.Transaction{}) {
		log.Printf("Webhook: Missing transaction: %d", stripeWebhookRequest.ID)
		return
	}

	updatedTransaction, err := paymentService.transactionRepository.UpdateTransaction(transactionEntity.TransactionID, newStatus)
	if err != nil {
		log.Printf("Webhook: Error while updating transaction: " + err.Error())
		return
	}

	log.Printf("Transaction completed %d", updatedTransaction.TransactionID)

	// send rmq message to complete an order
	rmqProducer := rmq.RMQProducer{
		ExchangeKey:      paymentService.conf.RMQExchangeKey,
		QueueName:        paymentService.conf.RMQQueueName,
		ConnectionString: paymentService.conf.RMQUrl,
	}

	var savedMessage = rmq2.PaymentDto{
		OrderId:        updatedTransaction.OrderID,
		UserId:         updatedTransaction.UserID,
		NewOrderStatus: newStatus,
		TransactionID:  updatedTransaction.TransactionID,
	}
	rmqProducer.ProduceMessage(&savedMessage)
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
	var transactionStatus = enums.STARTED
	var stripeResponse stripe.StripeResponse
	stripeResponse = paymentService.stripeService.StartStripePayment(
		orderEntity.Quantity,
		productEntity.ProductName,
		productEntity.ProductPrice,
	)
	if stripeResponse.Status != "success" {
		transactionStatus = enums.FAILED
		return response.PaymentResponse{Status: false, Message: "error on stripe side"}, nil
	} else {
		transactionStatus = enums.STARTED
	}

	// insert transaction completed
	var transaction model.Transaction
	transaction, err = paymentService.transactionRepository.SaveTransaction(paymentRequest.UserID, paymentRequest.OrderID,
		getCalculatedAmount(orderEntity.Quantity, productEntity.ProductPrice), transactionStatus, stripeResponse.ExternalTransactionID)
	if err != nil {
		return response.PaymentResponse{Status: false, Message: "Transaction error"}, err
	}

	log.Printf("Transaction %d started", transaction.TransactionID)
	return response.PaymentResponse{Status: true, Message: "success"}, nil
}

func getCalculatedAmount(quantity int, price float32) float32 {
	calculatedAmount := float32(quantity) * price
	return calculatedAmount
}
