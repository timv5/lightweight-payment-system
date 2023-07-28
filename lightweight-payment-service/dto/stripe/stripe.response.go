package stripe

type StripeResponse struct {
	Status      string
	Price       float32
	Quantity    int
	ProductName string
}
