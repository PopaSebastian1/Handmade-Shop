// src/app/services/product.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environment';
import { Product } from '../../models/product.model';
import { JwtDecoderService } from '../jwt-decoder/jwt-decoder.service';
@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient, private jwtDecoder:JwtDecoderService) {}

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  addUserToProduct(productId: number, userId: number, quantity: number = 1): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${productId}/users/${userId}?quantity=${quantity}`, {});
  }

  // New method for seller to add product for sale
  addProductForSale(sellerId: number, product: Product): Observable<Product> {
    console.log(product);
    return this.http.post<Product>(`${this.apiUrl}/seller/${sellerId}`, product);
  }

  // New method to associate user with product (generic)
  associateUserWithProduct(productId: number, userId: number, quantity: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/${productId}/user/${userId}?quantity=${quantity}`,
      {},
      { responseType: 'text' as 'json' } 
    );
  }  
  
  // New method to get products with user-specific quantities
  getProductsForUserWithQuantities(userId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/user/${userId}`);
  }
}
