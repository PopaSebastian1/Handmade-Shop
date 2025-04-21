import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, from, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { GoogleResponse } from '../../models/GoogleResponse';
import { ServerResponse } from '../../models/ServerResponse';
import { Router } from '@angular/router';
import { DataService } from '../../data.service';
// Extinde interfața Window pentru a include obiectul google
declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: any) => void;
          renderButton: (element: HTMLElement | null, options: any) => void;
          prompt: () => void;
        }
      }
    };
    handleCredentialResponse?: (response: GoogleResponse) => void;
  }
}

@Injectable({
  providedIn: 'root'
})
export class GoogleAuthService {
  private googleClientId = '251639296822-2rts40g7g70i0lsfv3d4uriic2597bbi.apps.googleusercontent.com';
  private baseURL = 'http://localhost:8080/Handmade-Shopping-1.0-SNAPSHOT/api/auth';
  private googleScriptLoaded = false;
  
  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: DataService
  ) {}
  

  /**
   * Încarcă scriptul Google Sign-In
   */
  loadGoogleScript(): Observable<boolean> {
    if (this.googleScriptLoaded || (typeof window !== 'undefined' && window.google && window.google.accounts)) {
      return of(true);
    }
    
    return from(new Promise<boolean>((resolve) => {
      const existingScript = document.querySelector('script[src="https://accounts.google.com/gsi/client"]');
      if (existingScript) {
        const checkGoogle = setInterval(() => {
          if (window.google && window.google.accounts) {
            clearInterval(checkGoogle);
            this.googleScriptLoaded = true;
            resolve(true);
          }
        }, 100);
        return;
      }
      
      // Adaugă scriptul dacă nu există
      const script = document.createElement('script');
      script.src = "https://accounts.google.com/gsi/client";
      script.async = true;
      script.defer = true;
      script.onload = () => {
        const checkGoogle = setInterval(() => {
          if (window.google && window.google.accounts) {
            clearInterval(checkGoogle);
            this.googleScriptLoaded = true;
            resolve(true);
          }
        }, 100);
      };
      script.onerror = () => {
        console.error('Failed to load Google API script');
        resolve(false);
      };
      document.head.appendChild(script);
    }));
  }

  /**
   * Inițializează butonul Google Sign-In pentru login
   */
  initializeLoginButton(container: HTMLElement | null): boolean {
    if (!window.google || !window.google.accounts) {
      console.error('Google API not loaded');
      return false;
    }

    try {
      // Inițializăm pentru login
      window.google.accounts.id.initialize({
        client_id: this.googleClientId,
        callback: (response: GoogleResponse) => {
          this.handleGoogleLogin(response).subscribe();
        }
      });

      if (container) {
        window.google.accounts.id.renderButton(
          container,
          { 
            theme: 'outline', 
            size: 'large', 
            text: 'signin_with', 
            shape: 'rectangular', 
            width: 240 
          }
        );
        return true;
      }
      return false;
    } catch (error) {
      console.error('Failed to initialize Google login button:', error);
      return false;
    }
  }

  /**
   * Inițializează butonul Google Sign-In pentru înregistrare
   */
  initializeSignupButton(container: HTMLElement | null): boolean {
    if (!window.google || !window.google.accounts) {
      console.error('Google API not loaded');
      return false;
    }

    try {
      // Inițializăm pentru înregistrare
      window.google.accounts.id.initialize({
        client_id: this.googleClientId,
        callback: (response: GoogleResponse) => {
          this.handleGoogleSignup(response).subscribe();
        }
      });

      if (container) {
        window.google.accounts.id.renderButton(
          container,
          { 
            theme: 'outline', 
            size: 'large', 
            text: 'signup_with', 
            shape: 'rectangular', 
            width: 240 
          }
        );
        return true;
      }
      return false;
    } catch (error) {
      console.error('Failed to initialize Google signup button:', error);
      return false;
    }
  }

  /**
   * Afișează dialogul Google Sign-In pentru login
   */
  promptGoogleLogin(): void {
    if (window.google && window.google.accounts) {
      window.google.accounts.id.initialize({
        client_id: this.googleClientId,
        callback: (response: GoogleResponse) => {
          this.handleGoogleLogin(response).subscribe();
        }
      });
      window.google.accounts.id.prompt();
    } else {
      console.error('Google API not loaded properly');
    }
  }

  /**
   * Afișează dialogul Google Sign-In pentru înregistrare
   */
  promptGoogleSignup(): void {
    if (window.google && window.google.accounts) {
      window.google.accounts.id.initialize({
        client_id: this.googleClientId,
        callback: (response: GoogleResponse) => {
          this.handleGoogleSignup(response).subscribe();
        }
      });
      window.google.accounts.id.prompt();
    } else {
      console.error('Google API not loaded properly');
    }
  }

  handleGoogleLogin(response: GoogleResponse): Observable<ServerResponse> {
    console.log('Google Sign-In login received:', response);
    
    const token = response.credential;
    console.log('Google token received (first 20 chars):', token.substring(0, 20) + '...');
    
    const apiUrl = `${this.baseURL}/google/login`;
    
    // Folosește formatul corect pentru backend
    const requestPayload = { idToken: token };
    
    return this.http.post<ServerResponse>(apiUrl, requestPayload, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(
      tap(response => {
        console.log('Server login response:', response);
        // Dacă primim un răspuns, considerăm autentificarea reușită
        if (response) {
          // Extragem email-ul din răspuns (dacă există)
          const email = response.email || 'google-user@example.com';
          this.authService.setUser(email);
          this.router.navigate(['/home']);
        }
      }),
      catchError(error => {
        console.error('Google login error:', error);
        throw error;
      })
    );
  }
  
handleGoogleSignup(response: GoogleResponse): Observable<ServerResponse> {
  console.log('Google Sign-Up received:', response);
  
  const token = response.credential;
  console.log('Google token received (first 20 chars):', token.substring(0, 20) + '...');
  
  const apiUrl = `${this.baseURL}/google/register`;
  
  // Construiește obiectul conform cu GoogleTokenRequest din backend
  const requestPayload = { idToken: token };
  
  console.log('Sending payload with idToken property');
  
  return this.http.post<ServerResponse>(apiUrl, requestPayload, {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }).pipe(
    tap(response => {
      console.log('Server register response:', response);
      
      // Dacă primim un răspuns, considerăm înregistrarea reușită
      if (response) {
        // Extragem email-ul din răspuns (dacă există)
        const email = response.email || 'google-user@example.com';
        this.authService.setUser(email);
        this.router.navigate(['/home']);
      }
    }),
    catchError(error => {
      console.error('Google signup error:', error);
      
      // Logger pentru debugging
      if (error instanceof HttpErrorResponse) {
        console.error('Status:', error.status, 'Status Text:', error.statusText);
        if (error.error && typeof error.error === 'string') {
          console.error('Server error message:', error.error);
        }
      }
      
      throw error;
    })
  );
}
}