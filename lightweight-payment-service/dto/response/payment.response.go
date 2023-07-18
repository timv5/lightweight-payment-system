package response

type PaymentResponse struct {
	OrderID int
	UserID  int
	Status  bool
	Message string
}
