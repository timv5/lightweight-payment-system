package handler

import (
	"gorm.io/gorm"
	"lightweight-payment-service/service"
)

type PaymentHandler struct {
	mysqlDB        *gorm.DB
	paymentService *service.PaymentService
}
