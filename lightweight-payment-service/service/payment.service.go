package service

import (
	"lightweight-payment-service/configs"
	"lightweight-payment-service/dto/request"
	"lightweight-payment-service/repository"
)

type PaymentServiceInterface interface {
	StartPayment(paymentRequest *request.PaymentRequest) (response *request.PaymentRequest)
}

type PaymentService struct {
	conf                  *configs.Config
	transactionRepository *repository.TransactionRepository
	orderRepository       *repository.OrderRepository
}

func NewPaymentService(config *configs.Config, transactionRepository *repository.TransactionRepository,
	orderRepository *repository.OrderRepository) *PaymentService {
	return &PaymentService{conf: config, transactionRepository: transactionRepository, orderRepository: orderRepository}
}
