package repository

import (
	"gorm.io/gorm"
	"lightweight-payment-service/model"
)

type TransactionRepositoryInterface interface {
	SaveTransaction(userId int, orderId int, transactionAmount float32, transactionStatus string, externalTransactionId string)
}

type TransactionRepository struct {
	mySQL *gorm.DB
}

func NewTransactionRepository(mySQL *gorm.DB) *TransactionRepository {
	return &TransactionRepository{mySQL: mySQL}
}

func (repo *TransactionRepository) SaveTransaction(userId int, orderId int, transactionAmount float32, transactionStatus string, externalTransactionId string) (model.Transaction, error) {
	createTransaction := model.Transaction{
		UserID:                userId,
		OrderID:               orderId,
		TransactionAmount:     transactionAmount,
		TransactionStatus:     transactionStatus,
		ExternalTransactionId: externalTransactionId,
	}

	savedTransaction := repo.mySQL.Create(&createTransaction)
	if savedTransaction.Error != nil {
		return model.Transaction{}, savedTransaction.Error
	} else {
		return createTransaction, nil
	}
}
