package rmq

type PaymentDto struct {
	OrderId        int    `json:"orderId" binding:"required"`
	UserId         int    `json:"userId" binding:"required"`
	NewOrderStatus string `json:"newOrderStatus" binding:"required"`
	TransactionID  int    `json:"transactionId" binding:"required"`
}
