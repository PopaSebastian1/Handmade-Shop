import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DataService } from '../data.service';
import { Subscription } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-pay',
  templateUrl: './pay.component.html',
  styleUrls: ['./pay.component.css']
})
export class PayComponent implements OnInit, OnDestroy {
  cart: any[] = [];
  cartSubscription: Subscription = new Subscription();
  nume: string = '';
  isProcessing: boolean = false;
  
  constructor(
    private dataService: DataService, 
    private router: Router,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit() {
  }

  ngOnDestroy() {
    this.cartSubscription.unsubscribe();
  }

  async onSubmit() {
    if (this.isProcessing) return;
    
    this.isProcessing = true;
    try {
      await this.clearCart();
      this.showSuccess('Payment processed successfully!');
      this.router.navigate(['/home']);
    } catch (error) {
      console.error('Payment error:', error);
      this.showError('Payment processing failed');
    } finally {
      this.isProcessing = false;
    }
  }

  private async clearCart(): Promise<void> {
    if (!this.cart.length) return;
    
    const clearPromises = this.cart.map(item => 
      this.dataService.decreaseCount(item).toPromise()
        .then(() => {
          item.count--;
          return item;
        })
        .catch(error => {
          console.error(`Error decreasing count for item ${item.id}:`, error);
          throw error;
        })
    );

    await Promise.all(clearPromises);
    this.cart = []; // Clear local cart after successful API calls
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: ['error-snackbar']
    });
  }
}