package rmqConsumer

import (
	"encoding/json"
	"github.com/streadway/amqp"
	"lightweight-payment-service/dto/rmq"
	"lightweight-payment-service/repository"
	"log"
)

func HandleMessage(msg amqp.Delivery, orderRepository *repository.OrderRepository,
	productStockRepository *repository.ProductStockRepository, transactionRepository *repository.TransactionRepository) {
	response := &rmq.Message{}
	err := json.Unmarshal(msg.Body, response)
	if err != nil {
		log.Printf("ERROR: fail unmarshl: %s", msg.Body)
		return
	}

	log.Printf("Successfully extracted: %s\n", response)

	// todo

}
