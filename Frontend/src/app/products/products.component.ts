import { Component, OnInit } from '@angular/core';
import { DataService } from '../data.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  cartItems: any[] = [];
  cartSubscription: Subscription = new Subscription();
  products: any[] = [];
  nume: string = '';
  showPopup = false;

  togglePopup() {
    this.showPopup = !this.showPopup;
  }

  constructor(private dataService: DataService, private router: Router) {
    this.nume = '';
  }

  addItemToCart(name: string, price: number, description: string, image: string, count: number = 1) {

    this.cartSubscription = this.dataService.getCart(this.nume).subscribe(cart => {
      this.cartItems = cart;
    });

    const item = this.cartItems.find(item => item.imagePath === image);

    if (item) {
      this.dataService.increaseCount(item).subscribe(response => {
        item.count++;
      });
    } else {
      this.dataService.addProductToUserByImagePath(image).subscribe(response => {
      });
      this.cartSubscription = this.dataService.getCart(this.nume).subscribe(cart => {
        this.cartItems = cart;
      });
    }
  }

  viewProductDetails(image: string) {
    this.router.navigate(['/product-details', image]);
  }

  addNewItem() {
    this.togglePopup();
  }

  ngOnInit() {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { refresh: boolean };

    if (state?.refresh) {
      this.refreshProducts();
    } else {
      this.dataService.getProducts().subscribe(products => {
        this.products = products;
      });
    }
  }

  refreshProducts() {
    this.dataService.getProducts().subscribe(products => {
      this.products = products;
      console.log('Products updated after delete');
    });
  }

  ngOnDestroy() {
    if (this.cartSubscription) {
      this.cartSubscription.unsubscribe();
    }
  }

  async addProduct(event: Event) {
    event.preventDefault();

    const productLink = (document.getElementById('productLink') as HTMLInputElement).value;
    const productName = (document.getElementById('productName') as HTMLInputElement).value;
    const productDescription = (document.getElementById('productDescription') as HTMLTextAreaElement).value;
    const productPrice = parseFloat((document.getElementById('productPrice') as HTMLInputElement).value);

    if (!productLink || !productName || !productDescription || isNaN(productPrice)) {
      alert('Toate câmpurile sunt obligatorii!');
      return;
    }

    const newProduct = {
      link: productLink,
      name: productName,
      description: productDescription,
      price: productPrice,
      id: Math.floor(100 + Math.random() * 900)
    };


    await this.dataService.addProduct(newProduct).toPromise();

    console.log('Produs adăugat:', newProduct);

    this.dataService.getProducts().subscribe(products => {
      this.products = products;
    });

    this.cartSubscription = this.dataService.getCart(this.nume).subscribe(cart => {
      this.cartItems = cart;
    });

    this.resetForm();
    this.togglePopup();
  }

  resetForm(): void {
    (document.getElementById('productLink') as HTMLInputElement).value = '';
    (document.getElementById('productName') as HTMLInputElement).value = '';
    (document.getElementById('productDescription') as HTMLTextAreaElement).value = '';
    (document.getElementById('productPrice') as HTMLInputElement).value = '';
  }
}