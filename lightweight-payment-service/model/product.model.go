package model

type Product struct {
	ProductId    int     `gorm:"type:bigint;primary_key" sql:"productId"`
	ProductPrice float32 `gorm:"not null" sql:"productPrice"`
	ProductName  string  `gorm:"not null" sql:"productName"`
}
