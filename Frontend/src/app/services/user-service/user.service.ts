import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../google-auth/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { User } from '../../models/user.model';
import { Router } from '@angular/router';
import {jwtDecode} from 'jwt-decode';
import { tap } from 'rxjs/operators';
import { JwtPayload } from '../../models/JwtPayload';
import { JwtDecoderService } from '../jwt-decoder/jwt-decoder.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = environment.apiUrl;
  private user = new BehaviorSubject<string>('');
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUserData = this.currentUserSubject.asObservable();

  currentUser = this.user.asObservable();
  nameCurrentUser: string = '';

  private loggedIn = new BehaviorSubject<boolean>(false);
  isLoggedIn: Observable<boolean> = this.loggedIn.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private jwtDecoder: JwtDecoderService
  ) {
    this.restoreSession(); // Verificăm dacă există un token salvat la inițializare
   }

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


  processJwtToken(token: string): void {
    try {
      // Decodează și salvează token-ul
      const payload = this.jwtDecoder.decodeJwt(token);
      this.jwtDecoder.saveToken(token);

      // Crează un obiect User din payload și setează utilizatorul curent
      const user = this.createUserFromPayload(payload);
      this.currentUserSubject.next(user);
      this.setUser(user.email);
    } catch (error) {
      console.error('Failed to process JWT token:', error);
    }
  }

  shouldDisplay(): boolean {
    const route = this.router.url;
    return this.loggedIn.getValue() && !['/login', '/home'].includes(route);
  }

  restoreSession(): boolean {
    // Verifică dacă există un token valid
    const payload = this.jwtDecoder.getPayloadIfValid();

    if (payload) {
      // Crează un obiect User din payload și setează utilizatorul curent
      const user: User = this.createUserFromPayload(payload);
      this.currentUserSubject.next(user);
      this.setUser(user.email);
      return true;
    }

    return false;
  }



  private createUserFromPayload(payload: JwtPayload): User {
    return {
      id: payload.userId,
      email: payload.sub,
      surname: payload.name,
      name: payload.surname,
      clientId: '',
      fullName: `${payload.name} ${payload.surname}`,
      roles: payload.roles,
      toSafeObject: function() {
        return {
          id: this.id,
          email: this.email,
          surname: this.surname,
          name: this.name,
          fullName: this.fullName,
          roles: this.roles
        };
      }
    };
  }

  // User management methods

  logoutUser(): void {
    // Șterge token-ul
    this.jwtDecoder.removeToken();

    // Resetează starea utilizatorului
    this.currentUserSubject.next(null);
    this.user.next('');
    this.loggedIn.next(false);

  }


  setUser(name: string) {
    this.user.next(name);
    this.nameCurrentUser = name;
    this.loggedIn.next(true);
  }

  setLoggedIn(value: boolean) {
    this.loggedIn.next(value);
  }

  setUserData(user: User) {
    this.currentUserSubject.next(user);
    this.setUser(user.email);
  }

  loginUser(email: string, password: string): Observable<any> {
    const params = new HttpParams()
      .set('email', email)
      .set('password', password);

    return this.http.post(`${this.baseUrl}/users/login`, null, {
      params,
      responseType: 'text'
    }).pipe(
      tap(token => {
        console.log('Login response received');
        this.processJwtToken(token);
      })
    );
  }


  signupUser(user: User): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post(`${this.baseUrl}/users`, user, {
      headers: headers,
      responseType: 'json'
    });
  }

  updateUser(user: User): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (!user.id) {
      throw new Error('User ID is required for update');
    }

    return this.http.put(`${this.baseUrl}/users/${user.id}`, user, {
      headers,
      responseType: 'json'
    });
  }

  userHasAnyRole(requiredRoles: string[]): boolean {
    const currentUser = this.currentUserSubject.value;
    if (!currentUser || !currentUser.roles) return false;
    return requiredRoles.some(role => currentUser.roles.includes(role));
  }

  userHasAllRoles(requiredRoles: string[]): boolean {
    const currentUser = this.currentUserSubject.value;
    if (!currentUser || !currentUser.roles) return false;
    return requiredRoles.every(role => currentUser.roles.includes(role));
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  updateUserRoles(userId: number, roles: string[]): Observable<string> {
    return this.http.put(
      `${this.baseUrl}/users/${userId}/roles`,
      roles,
      { responseType: 'text' }
    ).pipe(
      tap(token => this.processJwtToken(token))
    );
  }
}
