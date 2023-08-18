# lightweight-payment-system
System, generating QR code from selected products, payment implemented with Stripe

## Description and features
As a small business owner (admin) can add new products to a system. When a client wants to pay for them, admin can for selected products generate a QA code.
Client scans QA code on the admin page and is taken to the Stripe payment page where he can complete payment.
Payment history is accessible on admin page.

## Technologies used
- Java 17
- Golang
- Docker
- RabbitMQ
- MySQL
- Angular

## Project scope: services and pps
- angular frontend web application for clients
- angular frontend web application for admins
- java backend application
- golang rabbitMQ consumer with Stripe implementation

## App life cycle
- Customer tells admin what he would like to buy
- Admin creates an order with selected products and quantities. Endpoint /api/order/create is called
- Endpoint /api/order/create returns a QR image
- Admin tells customer to scan QR image, checks details and completes payment
- Customer scans QR image and is taken to the http://localhost:4000/{userId}/{orderId}
- on http://localhost:4000/{userId}/{orderId} 
- Customer can see current order details. Endpoint /api/order/details is called
- Customer clicks on "complete payment"
- Endpoint /startPayment is called (golang microservice), customer finishes payment on stripe site
- Once the payment is completed golang service (lightweight-payment-service) publishes message on RMQ, java service (be-lightweight-payment-system) consumes that message and completes an order - this is only for admin 
