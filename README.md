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
