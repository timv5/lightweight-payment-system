package repository

import (
	"gorm.io/gorm"
	"lightweight-payment-service/model"
)

type ProductRepositoryInterface interface {
	GetProduct(productId int) (model.Product, error)
}

type ProductRepository struct {
	mySQL *gorm.DB
}

func NewProductRepository(mySQL *gorm.DB) *ProductRepository {
	return &ProductRepository{mySQL: mySQL}
}

func (repo *ProductRepository) GetProduct(productId int) (model.Product, error) {
	var product model.Product
	repo.mySQL.Raw("select * from products where product_id = ?", productId).Scan(&product)
	if (model.Product{} == product) {
		return model.Product{}, nil
	} else {
		return product, nil
	}
}
