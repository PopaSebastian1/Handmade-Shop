import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from '../data.service';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-pay',
  templateUrl: './pay.component.html',
  styleUrl: './pay.component.css'
})
export class PayComponent {
  cart: any[] = [];
  cartSubscription: Subscription = new Subscription();
  nume: string ='';
  
  constructor(private dataService: DataService, private router: Router)
  {
    this.nume = '';
  } 
  
  ngOnInit() {
    this.cartSubscription = this.dataService.getCart(this.nume).subscribe(cart => {
    this.cart = cart;
    });
  }

  onSubmit() {
    this.clearCart();
    this.router.navigate(['/home']);
  }

  async clearCart() {
    for (const item of this.cart) {
      while(item.count >= 0) {
        await this.decreaseCount(item);
      }
    }
  }

  async decreaseCount(item: any) {
    await this.dataService.decreaseCount(item).toPromise();
    item.count--;
    if(item.count == 0) {
      const cart = await this.dataService.getCart(this.nume).toPromise();
      if (cart) {
        this.cart = cart;
      }
    }
  }
}
