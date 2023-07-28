package route

import (
	"github.com/gin-gonic/gin"
	"lightweight-payment-service/handler"
)

type PaymentRouteHandler struct {
	paymentHandler handler.PaymentHandler
}

func NewPaymentRouteHandler(paymentHandler handler.PaymentHandler) PaymentRouteHandler {
	return PaymentRouteHandler{paymentHandler: paymentHandler}
}

func (paymentHandler *PaymentRouteHandler) PaymentRoute(group *gin.RouterGroup) {
	router := group.Group("payment")
	router.POST("/startPayment", paymentHandler.paymentHandler.StartPayment)
}
