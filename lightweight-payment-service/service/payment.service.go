package service

import (
	"lightweight-payment-service/configs"
	"lightweight-payment-service/dto/request"
	"lightweight-payment-service/dto/response"
	"lightweight-payment-service/repository"
)

type PaymentServiceInterface interface {
	StartPayment(paymentRequest *request.PaymentRequest) (response.PaymentResponse, error)
}

type PaymentService struct {
	conf                   *configs.Config
	transactionRepository  *repository.TransactionRepository
	orderRepository        *repository.OrderRepository
	productStockRepository *repository.ProductStockRepository
}

func NewPaymentService(config *configs.Config, transactionRepository *repository.TransactionRepository,
	orderRepository *repository.OrderRepository, productStockRepository *repository.ProductStockRepository) *PaymentService {
	return &PaymentService{conf: config, transactionRepository: transactionRepository, orderRepository: orderRepository, productStockRepository: productStockRepository}
}

func (paymentService *PaymentService) StartPayment(paymentRequest *request.PaymentRequest) (response.PaymentResponse, error) {
	// check order - validation
	// check product & stock

	return response.PaymentResponse{Status: true, Message: "test"}, nil
}
