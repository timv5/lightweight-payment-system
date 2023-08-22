package repository

import (
	"gorm.io/gorm"
	"lightweight-payment-service/model"
)

type TransactionRepositoryInterface interface {
	SaveTransaction(userId int, orderId int, transactionAmount float32, transactionStatus string, externalTransactionId string) (model.Transaction, error)
	GetTransactionByExternalId(externalTransactionId int) model.Transaction
	UpdateTransaction(transactionId int, status string) (model.Transaction, error)
}

type TransactionRepository struct {
	mySQL *gorm.DB
}

func NewTransactionRepository(mySQL *gorm.DB) *TransactionRepository {
	return &TransactionRepository{mySQL: mySQL}
}

func (repo *TransactionRepository) SaveTransaction(userId int, orderId int, transactionAmount float32,
	transactionStatus string, externalTransactionId string) (model.Transaction, error) {
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

func (repo *TransactionRepository) GetTransactionByExternalId(externalTransactionId int) model.Transaction {
	var transaction model.Transaction
	repo.mySQL.Raw("select * from transactions where external_transaction_id = ?", externalTransactionId).Scan(&transaction)
	if (model.Transaction{} == transaction) {
		return model.Transaction{}
	} else {
		return transaction
	}
}

func (repo *TransactionRepository) UpdateTransaction(transactionId int, status string) (model.Transaction, error) {
	updatedTransaction := repo.mySQL.Model(&model.Transaction{}).Where("transactionId = ?", transactionId).Updates(model.Transaction{TransactionStatus: status})
	if updatedTransaction.Error != nil {
		return model.Transaction{}, updatedTransaction.Error
	} else {
		return model.Transaction{TransactionID: transactionId}, nil
	}
}
