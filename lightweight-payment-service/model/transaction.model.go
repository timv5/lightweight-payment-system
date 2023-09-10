package model

type Transaction struct {
	TransactionID         int     `gorm:"type:bigint;primary_key" sql:"transactionId"`
	OrderID               int     `gorm:"not null" sql:"orderId"`
	TransactionAmount     float32 `gorm:"not null" sql:"transactionAmount"`
	TransactionStatus     string  `gorm:"not null" sql:"transactionStatus"`
	ExternalTransactionId string  `gorm:"not null" sql:"externalTransactionId"`
}
