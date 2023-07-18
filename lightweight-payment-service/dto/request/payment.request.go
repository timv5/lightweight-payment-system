package request

type PaymentRequest struct {
	OrderID int `json:"orderId" binding:"required"`
	UserID  int `json:"userId" binding:"required"`
}
