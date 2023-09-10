package rmq

type PaymentDto struct {
	OrderId        int    `json:"orderId" binding:"required"`
	NewOrderStatus string `json:"newOrderStatus" binding:"required"`
	TransactionID  int    `json:"transactionId" binding:"required"`
}
