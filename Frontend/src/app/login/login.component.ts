import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../models/user.model';
import { GoogleAuthService } from '../../app/services/google-auth/google-auth.service';
import { UserService } from '../services/user-service/user.service';

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
    private userService: UserService,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder,
    private googleAuthService: GoogleAuthService
  ) { }

  ngOnInit(): void { }

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

  onLogin(event: Event): void {
    event.preventDefault();
  
    if (this.loginForm.invalid) return;
  
    const { email, password } = this.loginForm.value;
  
    this.userService.loginUser(email!, password!).subscribe({
      next: (user: User) => {
        this.userService.setUserData(user); // salvează tot userul
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Login error:', error);
        this.togglePopup();
      }
    });
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

    if (this.signupForm.invalid) return;

    const { name, surename, email, password } = this.signupForm.value;

    if (!name || !surename || !email || !password) return;

    const newUser = new User(
      name,
      surename,
      email,
      ["viewer"],
      password,
    );

    this.userService.signupUser(newUser).subscribe({
      next: () => this.handleSuccessfulAuth(email!),
      error: (error) => {
        console.error('Signup error:', error);
        this.togglePopup();
      }
    });
  }

  private handleSuccessfulAuth(email: string): void {
    this.userService.setUser(email);
    this.router.navigate(['/home']);
  }
}