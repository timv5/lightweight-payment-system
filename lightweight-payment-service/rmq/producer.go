package rmq

import (
	"encoding/json"
	uuid "github.com/satori/go.uuid"
	"github.com/streadway/amqp"
	"lightweight-payment-service/dto/rmq"
	"log"
	"time"
)

type RMQProducer struct {
	QueueName        string
	ConnectionString string
	ExchangeKey      string
}

func (pr RMQProducer) ProduceMessage(paymentDto *rmq.PaymentDto) {
	// set rmq
	conn, err := amqp.Dial(pr.ConnectionString)
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

	queue, err := ch.QueueDeclare(pr.QueueName, true, false, false, false, nil)
	if err != nil {
		panic("Cannot connect to Queue: " + err.Error())
	}
	log.Println(queue)

	// prepare data
	message := rmq.Message{Date: time.Now(), Message: *paymentDto, Identifier: uuid.NewV4().String()}
	stringMessage, err := json.Marshal(message)
	if err != nil {
		panic("Cannot marshal message to string")
	}

	err = ch.Publish(
		"",
		pr.QueueName,
		false,
		false,
		amqp.Publishing{
			ContentType: "application/json",
			Body:        []byte(string(stringMessage)),
		},
	)
	if err != nil {
		panic("Cannot publish")
	}

	log.Println("Successfully published message")
}
