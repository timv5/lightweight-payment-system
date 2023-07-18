import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CancelPaymentComponent } from './cancel-payment/cancel-payment.component';
import { SuccessPaymentComponent } from './success-payment/success-payment.component';

@NgModule({
  declarations: [
    AppComponent,
    CancelPaymentComponent,
    SuccessPaymentComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NoopAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
