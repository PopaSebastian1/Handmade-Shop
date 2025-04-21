import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../google-auth/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { User } from '../../models/user.model';
import { Router } from '@angular/router';

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
    private router: Router
  ) { }
  shouldDisplay(): boolean {
    const route = this.router.url;
    return this.loggedIn.getValue() && !['/login', '/home'].includes(route);
  }

  // User management methods

  logoutUser(): void {
    this.currentUserSubject.next(null); // Resetează utilizatorul curent
    this.user.next(''); // Resetează numele utilizatorului
    this.loggedIn.next(false); // Setează utilizatorul ca delogat
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

  loginUser(email: string, password: string): Observable<User> {
    const params = new HttpParams()
      .set('email', email)
      .set('password', password);

    return this.http.post<User>(`${this.baseUrl}/users/login`, null, { params });
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

  updateUserRoles(userId: number, roles: string[]): Observable<any> {
    return this.http.put(`${this.baseUrl}/users/${userId}/roles`, roles);
  }
}