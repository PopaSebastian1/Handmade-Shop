import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './home/home.component';
import { AboutComponent } from './about/about.component';
import { ProductsComponent } from './products/products.component';
import { CartComponent } from './cart/cart.component';
import { LoginComponent } from './login/login.component';
import { PayComponent } from './pay/pay.component';
import { authGuard } from './services/auth/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  { path: 'about', component: AboutComponent, canActivate: [authGuard] },
  { path: 'products', component: ProductsComponent, canActivate: [authGuard], data: { roles: ['seller', 'buyer'] } },
  { path: 'cart', component: CartComponent, canActivate: [authGuard], data: { roles: ['buyer'] } },
  { path: 'pay', component: PayComponent, canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
