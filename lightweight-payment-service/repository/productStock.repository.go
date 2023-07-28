package repository

import (
	"gorm.io/gorm"
	"lightweight-payment-service/model"
)

type ProductStockRepositoryInterface interface {
	GetProductStock(productId int) (model.ProductStock, error)
}

type ProductStockRepository struct {
	mySQL *gorm.DB
}

func NewProductStockRepository(mySQL *gorm.DB) *ProductStockRepository {
	return &ProductStockRepository{mySQL: mySQL}
}

func (repo *ProductStockRepository) GetProductStock(productId int) (model.ProductStock, error) {
	var productStock model.ProductStock
	repo.mySQL.Raw("select product_stock_id, product_id, quantity from products_stock where product_id = ?", productId).Scan(&productStock)
	if (model.ProductStock{} == productStock) {
		return model.ProductStock{}, nil
	} else {
		return productStock, nil
	}
}
