# lightweight-payment-system
System, generating QR code from selected products, payment implemented with Stripe

## Description and features
As a small business owner (admin) can add new products to a system. When a client wants to pay for them, admin can for selected products generate a QA code.
Client scans QA code on the admin page and is taken to the Stripe payment page where he can complete payment.
Payment history is accessible on admin page. It only contains backend code, but it's easy to integrate with your FE project. 

## Technologies used
- Java 17
- Golang
- Docker
- RabbitMQ
- MySQL

## Project scope: services and pps
- java backend application
- golang rabbitMQ consumer with Stripe implementation

## App life cycle
- Customer tells admin what he would like to buy
- Admin has listed products and quantities in admin app
- Admin creates an order with selected products and quantities. Endpoint /api/order/create is called
- Endpoint /api/order/create returns a QR image
- Admin tells customer to scan QR image, checks details and completes payment
- Customer scans QR image and is taken to the FE page located on http://localhost:4000/{orderId}
- on http://localhost:4000/{orderId} Customer can see current order details. Endpoint /api/order/details is called - it only shows orders that are not completed
so that this info is not accessible afterwards
- Customer clicks on "complete payment"
- Endpoint /startPayment is called (golang microservice), customer finishes payment on stripe site
- Once the payment is completed golang service (lightweight-payment-service) publishes message on RMQ, java service (be-lightweight-payment-system) consumes that message and completes an order - this is only for admin 

## How to run a project
- add a stripe secret token in app.env 
- in a root directory run: docker-compose -f docker-compose up -d
- run java and golang repository

## Endpoint definitions
be-lightweight-payment-system
- POST /api/order/create {"productId": 1,"quantity": 1}
- GET /api/order/list
- GET /api/order/details

lightweight-payment-service
- POST /api/payment/startPayment {"orderId": 1,"token": "token"}
- POST /api/payment/completePayment {"id": 1,"created": "2023-09-10T12:34:56Z","type": "payment_intent.canceled"}