import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {CancelPaymentComponent} from "./cancel-payment/cancel-payment.component";
import {SuccessPaymentComponent} from "./success-payment/success-payment.component";
import {CheckoutPaymentComponent} from "./checkout-payment/checkout-payment.component";

const routes: Routes = [
  {path: 'cancel', component: CancelPaymentComponent},
  {path: 'success', component: SuccessPaymentComponent},
  {path: 'checkout', component: CheckoutPaymentComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
