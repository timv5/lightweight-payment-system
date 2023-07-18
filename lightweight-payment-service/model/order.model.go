package model

type Order struct {
	OrderID     int    `gorm:"type:bigint;primary_key" sql:"orderId"`
	UserID      int    `gorm:"not null" sql:"userId"`
	ProductID   int    `gorm:"not null" sql:"orderId"`
	Quantity    int    `gorm:"not null" sql:"quantity"`
	OrderStatus string `gorm:"not null" sql:"orderStatus"`
	QRImage     byte   `sql:"qrImage"`
}
