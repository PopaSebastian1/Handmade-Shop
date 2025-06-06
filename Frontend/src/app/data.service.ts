import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

interface Item {
  name: string;
  price: number;
  count: number;
  description: string;
  imagePath: string;
}

interface ProductData {
  produs: {
    productName: string;
    price: number;
    quantity: number;
    description: string;
    imagePath: string;
    id: number;
  };
  quantity: number;
}


@Injectable({
  providedIn: 'root'
})
export class DataService {
  private baseUrl = 'http://54.166.166.136:8080/Handmade-Shopping-1.0-SNAPSHOT/api/';
  private user = new BehaviorSubject<string>('');
  currentUser = this.user.asObservable();
  nameCurrentUser: string = '';

  private loggedIn = new BehaviorSubject<boolean>(false);
  isLoggedIn: Observable<boolean> = this.loggedIn.asObservable();

  constructor(private router: Router, private http: HttpClient) {
  }

  getProducts(): Observable<Item[]> {
    const productsUrl = `${this.baseUrl}/Products/getProducts`; 
    return this.http.get<Item[]>(productsUrl);
  }

  addProduct(productAny: any): Observable<any> {

    const product: ProductData = {
      produs: {
        productName: productAny.name,
        price: productAny.price,
        quantity: 10,
        description: productAny.description,
        imagePath: productAny.link,
        id: productAny.id,
      },
      quantity: 1
    };

    const addProductUrl = `${this.baseUrl}/Products/addProduct`;
    return this.http.post(addProductUrl, product.produs);
  }

  updateProduct(name: string, productAny: any): Observable<any> {
    const product: ProductData = {
      produs: {
        productName: productAny.name,
        price: productAny.price,
        quantity: 10,
        description: productAny.description,
        imagePath: productAny.link,
        id: productAny.id,
      },
      quantity: 1
    };
    const updateProductUrl = `${this.baseUrl}/Products/updateProduct?name=${encodeURIComponent(name)}`;
    return this.http.put(updateProductUrl, product.produs);
  }

  deleteProduct(id: number): Observable<any> {
    const deleteProductUrl = `${this.baseUrl}/Products/deleteProduct?id=${id}`;
    return this.http.delete(deleteProductUrl);
  }

  addProductToUserByImagePath(imagePath: string): Observable<any> {
    const decreaseUrl = `${this.baseUrl}/AddProductToUserByImagePath?username=${this.nameCurrentUser}&imagePath=${encodeURIComponent(imagePath)}`;
    return this.http.post(decreaseUrl, {});
  }

  getCart(currentUser: any): Observable<Item[]> {
    const cartUrl = `${this.baseUrl}/UsersProducts?Name=${this.nameCurrentUser}`;
    return this.http.get<ProductData[]>(cartUrl).pipe(
      map(products => products
        .map((p: ProductData) => ({
          name: p.produs.productName,
          price: p.produs.price,
          count: p.quantity,
          description: p.produs.description,
          imagePath: p.produs.imagePath
        }))
        .sort((a, b) => a.name.localeCompare(b.name)) 
      )
    );
  }

  shouldDisplay(): boolean {
    const route = this.router.url;
    return this.loggedIn.getValue() && !['/login', '/home'].includes(route);
  }

  setUser(name: string) {
    this.user.next(name);
    this.nameCurrentUser = name;
    this.loggedIn.next(true);
  }

  setLoggedIn(value: boolean) { 
    this.loggedIn.next(value);
  }

  increaseCount(item: Item): Observable<any> {
    const increaseUrl = `${this.baseUrl}/IncreaseQuantity?name=${this.nameCurrentUser}&imagePath=${encodeURIComponent(item.imagePath)}`;
    return this.http.post(increaseUrl, {});
  }

  decreaseCount(item: Item): Observable<any> {
    const decreaseUrl = `${this.baseUrl}/DecreaseQuantity?name=${this.nameCurrentUser}&imagePath=${encodeURIComponent(item.imagePath)}`;
    return this.http.post(decreaseUrl, {});
  }

  addProductWithQuantity(imagePath: string, quantity: number): Observable<any> {
    const addProductUrl = `${this.baseUrl}/AddProductWithQuantity?name=${encodeURIComponent(this.nameCurrentUser)}&imagePath=${encodeURIComponent(imagePath)}&quantity=${quantity}`;
    return this.http.post(addProductUrl, {});
  }

  removeProductFromCart(item: Item): Observable<any> {
    const decreaseUrll = `${this.baseUrl}/DecreaseProductWithQuantity?name=${this.nameCurrentUser}&imagePath=${encodeURIComponent(item.imagePath)}`;
    return this.http.post(decreaseUrll, {});
  }
  
  clearCart(item: string[]): Observable<any> {
    const imagePaths = item.map(imagePath => `imagePath=${encodeURIComponent(imagePath)}`).join('&');
    const decreaseUrl = `${this.baseUrl}/ClearCart?name=${this.nameCurrentUser}&${imagePaths}`;
    return this.http.post(decreaseUrl, {});
  }
  

  removeItemFromCart(item: Item) {

    while (item.count > 0) {
      item.count--;
      this.decreaseCount(item).subscribe();
    }
  }

}
