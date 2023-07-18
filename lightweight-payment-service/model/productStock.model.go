package model

type ProductStock struct {
	ProductStockID int `gorm:"type:bigint;primary_key" sql:"productStockId"`
	ProductID      int `gorm:"not null" sql:"productId"`
	Quantity       int `gorm:"not null" sql:"quantity"`
}
