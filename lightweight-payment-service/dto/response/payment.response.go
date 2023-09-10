package response

type PaymentResponse struct {
	OrderID int
	Status  bool
	Message string
}
