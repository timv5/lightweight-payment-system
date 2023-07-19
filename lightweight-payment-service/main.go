package main

import (
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/streadway/amqp"
	"lightweight-payment-service/configs"
	"lightweight-payment-service/handler"
	"lightweight-payment-service/repository"
	rmq2 "lightweight-payment-service/rmqConsumer"
	"lightweight-payment-service/route"
	"lightweight-payment-service/service"
	"log"
)

var (
	server                 *gin.Engine
	PaymentController      handler.PaymentHandler
	PaymentRouteController route.PaymentRouteHandler
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

	paymentService := service.NewPaymentService(&config, transactionRepository, orderRepository, productStockRepository)

	PaymentController = handler.NewPaymentHandler(configs.DB, paymentService)
	PaymentRouteController = route.NewPaymentRouteHandler(PaymentController)

	server = gin.Default()
	corsConfig := cors.DefaultConfig()
	corsConfig.AllowOrigins = []string{"http://localhost:8081", config.ClientOrigin}
	corsConfig.AllowCredentials = true
	server.Use(cors.New(corsConfig))
	router := server.Group("/api")
	PaymentRouteController.PaymentRoute(router)

	initializeRMQ(&config, orderRepository, productStockRepository, transactionRepository)
}

func initializeRMQ(config *configs.Config, orderRepository *repository.OrderRepository,
	productStockRepository *repository.ProductStockRepository, transactionRepository *repository.TransactionRepository) {
	// set rmqConsumer
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
			go rmq2.HandleMessage(m, orderRepository, productStockRepository, transactionRepository)
		}
	}()
	<-forever
}
