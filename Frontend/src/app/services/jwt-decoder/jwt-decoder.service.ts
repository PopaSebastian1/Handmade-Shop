import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { JwtPayload } from '../../models/JwtPayload';

@Injectable({
  providedIn: 'root'
})
export class JwtDecoderService {
  private readonly TOKEN_KEY = 'jwt_token';
  
  constructor() { }

  /**
   * Decodifică un token JWT
   */
  decodeJwt(token: string): JwtPayload {
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      console.log('Decoded JWT:', decoded);
      return decoded;
    } catch (error) {
      console.error('Error decoding JWT:', error);
      throw new Error('Invalid JWT token');
    }
  }

  /**
   * Salvează token-ul în localStorage
   */
  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * Obține token-ul din localStorage
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Șterge token-ul din localStorage
   */
  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  /**
   * Verifică dacă există un token valid în localStorage
   */
  hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      const decoded = this.decodeJwt(token);
      const currentTime = Date.now() / 1000;
      return decoded.exp > currentTime;
    } catch (error) {
      console.error('Invalid token:', error);
      this.removeToken();
      return false;
    }
  }

  /**
   * Obține payload-ul din token dacă acesta este valid
   */
  getPayloadIfValid(): JwtPayload | null {
    const token = this.getToken();
    if (!token) return null;
    
    try {
      const decoded = this.decodeJwt(token);
      const currentTime = Date.now() / 1000;
      
      if (decoded.exp > currentTime) {
        return decoded;
      }
      
      this.removeToken();
      return null;
    } catch (error) {
      console.error('Invalid token:', error);
      this.removeToken();
      return null;
    }
  }
}