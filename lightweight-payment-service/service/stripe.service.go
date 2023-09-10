package service

import (
	outerStripe "github.com/stripe/stripe-go"
	"github.com/stripe/stripe-go/charge"
	"lightweight-payment-service/configs"
	"lightweight-payment-service/dto/stripe"
	"log"
)

type StripeServiceInterface interface {
	StartStripePayment(quantity int, productName string, price float32, token string) stripe.StripeResponse
}

type StripeService struct {
	conf *configs.Config
}

func NewStripeService(config *configs.Config) *StripeService {
	return &StripeService{conf: config}
}

func (s StripeService) StartStripePayment(quantity int, productName string, price float32, token string) stripe.StripeResponse {
	calculatedAmount := float32(quantity) * price
	stripeRequest := &outerStripe.ChargeParams{
		Amount:      outerStripe.Int64(int64(calculatedAmount)),
		Currency:    outerStripe.String(string(outerStripe.CurrencyUSD)),
		Description: outerStripe.String(productName),
	}

	err := stripeRequest.SetSource(token)
	if err != nil {
		log.Println("Error setting token: " + err.Error())
		return stripe.StripeResponse{Status: "failed", Price: price, Quantity: quantity, ProductName: productName}
	}
	outerStripe.Key = s.conf.StripeSecretKey

	// call to the stripe side
	status := "success"
	ch, err := charge.New(stripeRequest)
	if err != nil {
		log.Println("Error on stripe side: " + err.Error())
		status = "error"
	} else {
		switch ch.Status {
		case "succeeded":
			status = "success"
		case "pending":
			status = "success"
		case "failed":
			status = "error"
		default:
			status = "failed"
		}
	}

	return stripe.StripeResponse{Status: status, Price: price, Quantity: quantity, ProductName: productName}
}
