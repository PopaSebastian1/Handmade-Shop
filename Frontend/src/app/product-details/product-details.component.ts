import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../data.service'; 
import { Subscription } from 'rxjs';

interface Item {
  name: string;
  price: number;
  count: number;
  imagePath: string;
}

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {
  product: any; 
  selectedQuantity = 1;
  cartItems: any[] = [];
  products: any[] = [];
  cartSubscription: Subscription = new Subscription();
  nume: string = '';
  isEditing: boolean = false;
  productToEdit: any = null;
  showPopup = false;
  link: string = '';

  togglePopup() {
    this.showPopup = !this.showPopup;
  }

  constructor(private route: ActivatedRoute, private dataService: DataService, private router: Router) {
    this.nume = '';
  }

  ngOnInit() {
    this.dataService.currentUser.subscribe(nume => this.nume = nume);
    const image = this.route.snapshot.paramMap.get('image');
    this.dataService.getProducts().subscribe(products => {
      this.product = products.find(product => product.imagePath === image);
    });
    this.cartSubscription = this.dataService.getCart(this.nume).subscribe(cart => {
      this.cartItems = cart;
    });
    this.dataService.getProducts().subscribe(products => {
      this.products = products;
    });
  }

  addToCart(product: any, quantity: number) {
    this.dataService.addProductWithQuantity(product.imagePath, quantity).subscribe(() => {
      console.log('Product added to cart');
    }, error => {
      console.error('Error adding product to cart', error);
    });
  }

  delete(product: any, quantity: number) {
    this.dataService.deleteProduct(product.id).subscribe(() => {
      console.log('Product deleted');
      this.router.navigate(['/products'], { state: { refresh: true } }); 
    });
  }
  

  edit(product: any, quantity: number) {
    this.productToEdit = product;
    this.togglePopup();
    this.link=product.imagePath;
    setTimeout(() => {
      this.populateForm(product);
    }, 0);
  }

  editProduct(event: Event): void {
    event.preventDefault();

    const productName = (document.getElementById('productName') as HTMLInputElement).value;
    const productDescription = (document.getElementById('productDescription') as HTMLTextAreaElement).value;
    const productPrice = parseFloat((document.getElementById('productPrice') as HTMLInputElement).value);

    if (!productName || !productDescription || isNaN(productPrice)) {
      alert('Toate câmpurile sunt obligatorii!');
      return;
    }

    const product = this.products.find(product => product.imagePath === this.link);
    if (product) {
      const newProduct = {
        link: this.link,
        name: productName,
        description: productDescription,
        price: productPrice,
        id: product.id
      };
      this.dataService.updateProduct(newProduct.link, newProduct).subscribe();
      this.product.productName = productName;
      this.product.description = productDescription;
      this.product.price = productPrice;
    }

    this.togglePopup();

    this.isEditing = false;
    this.productToEdit = null;

  }

  populateForm(product: any): void {
    const productNameElement = document.getElementById('productName') as HTMLInputElement;
    const productDescriptionElement = document.getElementById('productDescription') as HTMLTextAreaElement;
    const productPriceElement = document.getElementById('productPrice') as HTMLInputElement;

    if (productNameElement) productNameElement.value = product.productName || '';
    if (productDescriptionElement) productDescriptionElement.value = product.description || '';
    if (productPriceElement) productPriceElement.value = product.price?.toString() || '';
  }


}