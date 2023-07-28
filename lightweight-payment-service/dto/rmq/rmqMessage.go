package rmq

import "time"

type Message struct {
	Date       time.Time  `json:"date" binding:"required"`
	Message    PaymentDto `json:"message" binding:"required"`
	Identifier string     `json:"identifier" binding:"required"`
}
