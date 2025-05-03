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
  products: Product[] = []; // All products
  cart: Product[] = []; // User's cart products with quantities
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
      image: ['', Validators.required],
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      quantity: [1, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit() {
    // Load all products
    this.productService.getAllProducts().subscribe(products => {
      this.products = products;
    });

    // Subscribe to current user changes
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
  }

  closeSuccessPopup() {
    this.showSuccessPopup = false;
  }  

  viewProductDetails(image: string) {
    this.router.navigate(['/product-details', image]);
  }

  addProduct(event: Event) {
    event.preventDefault();

    // Get values using the correct IDs from your template
    const productImage = (document.getElementById('productImage') as HTMLInputElement).value;
    const productName = (document.getElementById('productName') as HTMLInputElement).value;
    const productDescription = (document.getElementById('productDescription') as HTMLTextAreaElement).value;
    const productPrice = parseFloat((document.getElementById('productPrice') as HTMLInputElement).value);
    const productQuantity = parseInt((document.getElementById('productQuantity') as HTMLInputElement).value);

    const currentUser = this.userService.getCurrentUser();
    if (!currentUser?.id) {
      console.error('No user logged in');
      return;
    }

    // Create new Product with all fields including quantity
    const newProduct = new Product(
      productName,
      productPrice,
      productQuantity, // Use the quantity from form
      productDescription,
      0, // Default rating
      productImage
    );

    this.productService.addProductForSale(
      currentUser.id,
      newProduct
    ).subscribe({
      next: () => {
        // Refresh products list
        this.productService.getAllProducts().subscribe(products => {
          this.products = products;
        });
        this.togglePopup();
      },
      error: (err) => {
        console.error('Error adding product:', err);
      }
    });
  }
}