package handler

import (
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
	"lightweight-payment-service/dto/request"
	"lightweight-payment-service/service"
	"net/http"
)

type PaymentHandler struct {
	mysqlDB        *gorm.DB
	paymentService *service.PaymentService
}

func NewPaymentHandler(mysqlDB *gorm.DB, paymentService *service.PaymentService) PaymentHandler {
	return PaymentHandler{
		mysqlDB:        mysqlDB,
		paymentService: paymentService,
	}
}

func (ph PaymentHandler) StartPayment(ctx *gin.Context) {
	var requestPayload *request.PaymentRequest
	if err := ctx.ShouldBindJSON(&requestPayload); err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"status": "error", "message": err.Error()})
		return
	}

	if requestPayload.OrderID == 0 || requestPayload.UserID == 0 {
		ctx.JSON(http.StatusBadRequest, gin.H{"status": "error", "message": "wrong request params"})
		return
	}

	paymentResponse, err := ph.paymentService.StartPayment(requestPayload)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"status:": "error", "message": err})
		return
	} else {
		ctx.JSON(http.StatusOK, paymentResponse)
	}
}
