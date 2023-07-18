package main

import (
	"encoding/json"
	"github.com/streadway/amqp"
	"lightweight-payment-service/configs"
	"lightweight-payment-service/dto/rmq"
	"lightweight-payment-service/repository"
	"log"
)

func main() {
	// set configs
	config, err := configs.LoadConfig(".")
	if err != nil {
		panic("Could not initialize app")
	}

	// connect to database
	configs.ConnectToDB(&config)

	// initialize repositories
	orderRepository := repository.NewOrderRepository(configs.DB)
	productStockRepository := repository.NewProductStockRepository(configs.DB)
	transactionRepository := repository.NewTransactionRepository(configs.DB)

	initializeRMQ(&config, orderRepository, productStockRepository, transactionRepository)
}

func initializeRMQ(config *configs.Config, orderRepository *repository.OrderRepository,
	productStockRepository *repository.ProductStockRepository, transactionRepository *repository.TransactionRepository) {
	// set rmq
	conn, err := amqp.Dial(config.RMQUrl)
	if err != nil {
		panic("Could not initialize RMQ")
	}
	defer conn.Close()

	log.Println("Successfully connected to RMQ")

	// connect to channel
	ch, err := conn.Channel()
	if err != nil {
		panic("Cannot connect to RMQ channel")
	}
	defer ch.Close()

	queue, err := ch.QueueDeclare(config.RMQQueueName, false, false, false, false, nil)
	if err != nil {
		panic("Cannot connect to Queue")
	}
	log.Println(queue)

	msg, err := ch.Consume(config.RMQQueueName, "", true, false, false, false, nil)

	forever := make(chan bool)
	go func() {
		for m := range msg {
			go handleMessage(m, orderRepository, productStockRepository, transactionRepository)
		}
	}()
	<-forever
}

func handleMessage(msg amqp.Delivery, orderRepository *repository.OrderRepository,
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
