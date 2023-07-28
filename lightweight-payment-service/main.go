package main

import (
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"lightweight-payment-service/configs"
	"lightweight-payment-service/handler"
	"lightweight-payment-service/repository"
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
	productRepository := repository.NewProductRepository(configs.DB)

	stripeService := service.NewStripeService(&config)
	paymentService := service.NewPaymentService(&config, transactionRepository, orderRepository, productStockRepository, productRepository, stripeService)

	PaymentController = handler.NewPaymentHandler(configs.DB, paymentService)
	PaymentRouteController = route.NewPaymentRouteHandler(PaymentController)

	server = gin.Default()
	corsConfig := cors.DefaultConfig()
	corsConfig.AllowOrigins = []string{"http://localhost:8081", config.ClientOrigin}
	corsConfig.AllowCredentials = true
	server.Use(cors.New(corsConfig))
	router := server.Group("/api")
	PaymentRouteController.PaymentRoute(router)

	log.Fatal(server.Run(":" + config.ServerPort))
}
