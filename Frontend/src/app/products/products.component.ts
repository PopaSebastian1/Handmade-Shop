import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { ProductService } from '../services/product-service/product.service';
import { Product } from '../models/product.model';
import { UserService } from '../services/user-service/user.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  cart: Product[] = [];
  showPopup = false;
  currentUserId: number | null = null;
  private userSubscription: Subscription | null = null;
  productForm: FormGroup;
  successMessage: string = '';
  showSuccessPopup: boolean = false;

  constructor(
    private productService: ProductService,
    private router: Router,
    private userService: UserService,
    private fb: FormBuilder
  ) {
    this.productForm = this.fb.group({
      image: ['', [
        Validators.required, 
        Validators.pattern(/^(http(s)?:\/\/)?([\w-]+\.)+[\w-]+(\/[\w- ;,./?%&=]*)?$/)
      ]],
      name: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(50)
      ]],
      description: ['', [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(500)
      ]],
      price: [0, [
        Validators.required,
        Validators.min(0.01),
        Validators.max(10000)
      ]],
      quantity: [1, [
        Validators.required,
        Validators.min(1),
        Validators.max(1000),
        Validators.pattern(/^[0-9]*$/)
      ]]
    });
  }

  ngOnInit() {
    this.productService.getAllProducts().subscribe(products => {
      this.products = products;
    });

    this.userSubscription = this.userService.currentUserData.subscribe(user => {
      if (user && user.id) {
        this.currentUserId = user.id;
        this.loadUserCart();
      } else {
        this.currentUserId = null;
        this.cart = [];
      }
    });
  }

  ngOnDestroy() {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  loadUserCart(): void {
    if (this.currentUserId) {
      this.productService.getProductsForUserWithQuantities(this.currentUserId)
        .subscribe({
          next: (cartProducts) => {
            this.cart = cartProducts;
          },
          error: (err) => {
            console.error('Error loading cart:', err);
          }
        });
    }
  }

  togglePopup() {
    this.showPopup = !this.showPopup;
    if (!this.showPopup) {
      this.productForm.reset({
        price: 0,
        quantity: 1
      });
    }
  }

  getCartQuantity(productId: number | undefined): number {
    const cartItem = this.cart.find(item => item.id === productId);
    return cartItem ? cartItem.quantity : 0;
  }

  addNewItem() {
    this.togglePopup();
  }

  addItemToCart(productId: number | undefined) {
    if (!this.currentUserId || productId === undefined) {
      console.error('No user logged in or invalid product');
      return;
    }

    const cartProduct = this.cart.find(p => p.id === productId);
    const currentQuantity = cartProduct ? cartProduct.quantity : 0;
    const newQuantity = currentQuantity + 1;

    this.productService.associateUserWithProduct(
      productId,
      this.currentUserId,
      newQuantity
    ).subscribe({
      next: (response) => {
        if (typeof response === 'string') {
          if (response !== '') {
            this.showTemporarySuccess(response);
          }
        }
        this.loadUserCart();
      },
      error: (err) => {
        if (err.status === 200) {
          this.loadUserCart();
        }
      }
    });
  }

  showTemporarySuccess(message: string) {
    this.successMessage = message;
    this.showSuccessPopup = true;
    setTimeout(() => {
      this.closeSuccessPopup();
    }, 3000);
  }

  closeSuccessPopup() {
    this.showSuccessPopup = false;
  }

  viewProductDetails(image: string) {
    this.router.navigate(['/product-details', image]);
  }

  addProduct(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    const { image, name, description, price, quantity } = this.productForm.value;

    const currentUser = this.userService.getCurrentUser();
    if (!currentUser?.id) {
      console.error('No user logged in');
      return;
    }

    const newProduct = new Product(
      name,
      price,
      quantity,
      description,
      0,
      image
    );

    this.productService.addProductForSale(currentUser.id, newProduct).subscribe({
      next: () => {
        this.productService.getAllProducts().subscribe(products => {
          this.products = products;
        });
        this.togglePopup();
        this.showTemporarySuccess('Product added successfully!');
      },
      error: (err) => {
        console.error('Error adding product:', err);
        this.showTemporarySuccess('Error adding product. Please try again.');
      }
    });
  }
}