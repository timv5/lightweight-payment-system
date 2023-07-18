import {Component, OnInit} from '@angular/core';
import {loadStripe} from "@stripe/stripe-js";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-checkout-payment',
  templateUrl: './checkout-payment.component.html',
  styleUrls: ['./checkout-payment.component.css']
})
export class CheckoutPaymentComponent {
  // We load  Stripe
  stripePromise = loadStripe(environment.stripe);

  constructor(private http:HttpClient) {
  }

  async pay(): Promise<void> {
    const payment = {
      name: 'Iphone',
      currency: 'usd',
      // amount on cents *10 => to be on dollar
      amount: 99900,
      quantity: '1',
      cancelUrl: 'http://localhost:4200/cancel',
      successUrl: 'http://localhost:4200/success',
    };

    const stripe = await this.stripePromise;

    this.http
      .post(`${environment.serverUrl}/payment`, payment)
      .subscribe((data: any) => {
        // @ts-ignore
        stripe.redirectToCheckout({
          sessionId: data.id,
        });
      });
  }
}
