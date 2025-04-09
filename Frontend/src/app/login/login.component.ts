import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { DataService } from '../data.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  baseURL: string = 'http://localhost:8080/Handmade-Shopping-1.0-SNAPSHOT/api/users';
  showPopup = false;

  togglePopup() {
    this.showPopup = !this.showPopup;
  }

  loginForm = this.fb.group({
    email: ['', Validators.required],
    password: ['', Validators.required],
  });

  signupForm = this.fb.group({
    name: ['', Validators.required],
    surename: ['', Validators.required],
    email: ['', Validators.required],
    password: ['', Validators.required],
  });

  constructor(
    private authService: DataService,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder
  ) { }

  loginUser(email: string, password: string) {
    const params = new HttpParams()
      .set('email', email)
      .set('password', password);
  
    return this.http.post(`${this.baseURL}/login`, null, {
      params: params,
      responseType: 'text' 
    });
  }
  
  onLogin(event: Event): void {
    event.preventDefault();
    if (this.loginForm.valid) {
      const email = this.loginForm.value.email;
      const password = this.loginForm.value.password;
      if (email && password) {
        this.loginUser(email, password).subscribe({
          next: (response: string) => {
            if (response === 'Invalid email or password') {
              this.togglePopup(); 
            } else {
              this.authService.setUser(email);
              this.router.navigate(['/home']);
            }
          },
          error: (error) => {
            console.error('Unexpected error:', error);
          }
        });
      }
    }
  }

  signupUser(user: User) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post(`${this.baseURL}`, user, {
      headers: headers,
      responseType: 'json'
    });
  }

  onSignup(event: Event): void {
    event.preventDefault();

    if (this.signupForm.valid) {
      const name = this.signupForm.value.name!;
      const surname = this.signupForm.value.surename!;
      const email = this.signupForm.value.email!;
      const password = this.signupForm.value.password!;
      const roles = ["viewer"];

      const newUser = new User(name, surname, email, password, roles);

      this.signupUser(newUser).subscribe({
        next: (response) => {
          console.log('Response:', response);
          if (response === 'User created') {
            this.authService.setUser(email);
            this.router.navigate(['/home']);
          }
        },
        error: (error) => {
          console.error('Error:', error);
        }
      });
    }
  }
}
