package request

type PaymentRequest struct {
	OrderID int    `json:"orderId" binding:"required"`
	Token   string `json:"token" binding:"required"`
}
