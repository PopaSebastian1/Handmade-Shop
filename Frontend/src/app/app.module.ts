import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AboutComponent } from './about/about.component';
import { ProductsComponent } from './products/products.component';
import { CartComponent } from './cart/cart.component';
import { MenuComponent } from './menu/menu.component';
import { PayComponent } from './pay/pay.component';
import { FormsModule } from '@angular/forms';
import { HasRoleDirective } from './has-role.directive';
import { AuthInterceptor } from './services/auth/auth.interceptor';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    AboutComponent,
    ProductsComponent,
    CartComponent,
    MenuComponent,
    PayComponent,
    HasRoleDirective,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    BrowserAnimationsModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
