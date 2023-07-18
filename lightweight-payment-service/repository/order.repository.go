package repository

import (
	"gorm.io/gorm"
	"lightweight-payment-service/model"
)

type OrderRepositoryInterface interface {
	UpdateOrderStatus(orderId int, newStatus string)
}

type OrderRepository struct {
	mySQL *gorm.DB
}

func NewOrderRepository(mySQL *gorm.DB) *OrderRepository {
	return &OrderRepository{mySQL: mySQL}
}

func (repo *OrderRepository) UpdateOrderStatus(orderId int, newStatus string) (model.Order, error) {
	updatedOrder := repo.mySQL.Model(&model.Order{}).Where("orderId = ?", orderId).Updates(model.Order{OrderStatus: newStatus})
	if updatedOrder.Error != nil {
		return model.Order{}, updatedOrder.Error
	} else {
		return model.Order{OrderID: orderId}, nil
	}
}
