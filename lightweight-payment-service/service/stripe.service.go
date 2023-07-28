package service

import (
	"lightweight-payment-service/configs"
	"lightweight-payment-service/dto/stripe"
)

type StripeServiceInterface interface {
	StartStripePayment(quantity int, productName string, price float32) stripe.StripeResponse
}

type StripeService struct {
	conf *configs.Config
}

func NewStripeService(config *configs.Config) *StripeService {
	return &StripeService{conf: config}
}

func (s StripeService) StartStripePayment(quantity int, productName string, price float32) stripe.StripeResponse {
	return stripe.StripeResponse{Status: "success", Price: price, Quantity: quantity, ProductName: productName}
}
