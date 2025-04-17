// login.component.ts
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { DataService } from '../data.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { User } from '../../models/user.model';
import { GoogleAuthService } from '../../app/services/google-auth/google-auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit, AfterViewInit {
  baseURLGoogle: string = 'http://localhost:8080/Handmade-Shopping-1.0-SNAPSHOT/api/auth';
  baseURL: string = 'http://localhost:8080/Handmade-Shopping-1.0-SNAPSHOT/api/users';
  showPopup = false;
  googleButtonRendered = false;

  togglePopup(): void {
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
    private fb: FormBuilder,
    private googleAuthService: GoogleAuthService // Injectăm noul serviciu
  ) { }

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    this.googleAuthService.loadGoogleScript().subscribe(success => {
      if (success) {
        this.initializeGoogleButtons();
      } else {
        console.error('Failed to load Google API script');
        this.googleButtonRendered = false;
      }
    });
  }

  initializeGoogleButtons(): void {
    const loginContainer = document.getElementById('google-login-btn-container');
    const loginSuccess = this.googleAuthService.initializeLoginButton(loginContainer);
    
    const signupContainer = document.getElementById('google-signup-btn-container');
    const signupSuccess = this.googleAuthService.initializeSignupButton(signupContainer);
    
    this.googleButtonRendered = loginSuccess && signupSuccess;
    console.log('Google buttons initialized:', this.googleButtonRendered);
  }

  manualGoogleLogin(): void {
    const loginForm = document.querySelector('.login-form');
    if (loginForm && window.getComputedStyle(loginForm).display !== 'none') {
      this.googleAuthService.promptGoogleLogin();
    } else {
      this.googleAuthService.promptGoogleSignup();
    }
  }

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
          next: (response: any): void => {
            console.log('Login response:', response);
            // Verifică dacă răspunsul indică o eroare cunoscută
            if (response === 'Invalid email or password') {
              this.togglePopup(); 
            } else {
              // Pentru orice alt răspuns, considerăm login-ul reușit
              this.authService.setUser(email);
              this.router.navigate(['/home']);
            }
          },
          error: (error: unknown): void => {
            console.error('Login error:', error);
            this.togglePopup();
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
        next: (response: any): void => {
          console.log('Signup response:', response);
          // Orice răspuns de succes ar trebui să ne ducă la pagina home
          // Nu mai verificăm doar 'User created', ci acceptăm orice răspuns pozitiv
          this.authService.setUser(email);
          this.router.navigate(['/home']);
        },
        error: (error: unknown): void => {
          console.error('Signup error:', error);
          // Aici ai putea adăuga un popup de eroare
          this.togglePopup();
        }
      });
    }
  }
}