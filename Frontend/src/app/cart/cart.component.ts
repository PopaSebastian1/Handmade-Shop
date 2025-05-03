import { Component, OnInit } from '@angular/core';
import { ProductService } from '../services/product-service/product.service';
import { UserService } from '../services/user-service/user.service';
import { Router } from '@angular/router';
import { Product } from '../models/product.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cart: Product[] = [];
  currentUserId: number | null = null;
  successMessage: string = '';
  showSuccessPopup: boolean = false;

  constructor(
    private productService: ProductService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit() {
    const user = this.userService.getCurrentUser();
    if (user?.id) {
      this.currentUserId = user.id;
      this.loadCart();
    }
  }

  loadCart() {
    if (this.currentUserId) {
      this.productService.getProductsForUserWithQuantities(this.currentUserId)
        .subscribe({
          next: (products) => {
            this.cart = products;
          },
          error: (err) => {
            console.error('Error loading cart:', err);
          }
        });
    }
  }

  navigateToPay() {
    this.router.navigate(['/pay']);
  }

  removeItemFromCart(productId: number | undefined) {
    if (!this.currentUserId || productId === undefined) return;

    this.productService.associateUserWithProduct(productId, this.currentUserId, 0)
      .subscribe({
        next: () => {
          this.loadCart(); // Refresh cart after removal
        },
        error: (err) => {
          if (err.status === 200) {
            console.warn('Received 200 with invalid JSON on remove. Refreshing cart anyway.');
            this.loadCart();
          } else {
            console.error('Error removing item:', err);
          }
        }
      });
  }

  totalCount() {
    return this.cart.reduce((total, item) => total + (item.quantity || 0), 0);
  }

  totalCart() {
    return this.cart.reduce((total, item) => total + (item.price * (item.quantity || 0)), 0);
  }

  clearCart() {
    if (!this.currentUserId) return;

    // Remove all items by setting quantity to 0
    const clearOperations = this.cart.map(item =>
      this.productService.associateUserWithProduct(item.id!, this.currentUserId!, 0)
    );

    // Execute all operations
    forkJoin(clearOperations).subscribe({
      next: () => {
        this.cart = []; // Clear local cart
      },
      error: (err) => {
        if (err.status === 200) {
          console.warn('Received 200 with invalid JSON on clear cart. Forcing local clear.');
          this.cart = [];
        } else {
          console.error('Error clearing cart:', err);
        }
      }
    });
  }

  increaseCount(product: Product) {
    if (!this.currentUserId || !product.id) return;

    const newQuantity = (product.quantity || 0) + 1;
    this.updateProductQuantity(product.id, newQuantity);
  }

  decreaseCount(product: Product) {
    if (!this.currentUserId || !product.id) return;

    const newQuantity = Math.max(0, (product.quantity || 0) - 1);
    this.updateProductQuantity(product.id, newQuantity);
  }

  showTemporarySuccess(message: string) {
    this.successMessage = message;
    this.showSuccessPopup = true;
  }

  closeSuccessPopup() {
    this.showSuccessPopup = false;
  }  

  updateProductQuantity(productId: number, quantity: number) {
    this.productService.associateUserWithProduct(
      productId,
      this.currentUserId!,
      quantity
    ).subscribe({
      next: (response) => {
        if (typeof response === 'string') {
          if (response !== '') {
            this.showTemporarySuccess(response);
          }
        }
        this.loadCart(); // Success path
      },
      error: (err) => {
        // Dacă e o eroare de parsare, dar status 200, trateaz-o ca succes
        if (err.status === 200) {
          console.warn('Received 200 with invalid JSON. Continuing...');
          this.loadCart();
        } else {
          console.error('Error updating quantity:', err);
        }
      }
    });
  }

  navigateToProducts() {
    this.router.navigate(['/products']);
  }
}