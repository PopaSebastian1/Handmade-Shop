import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../models/user.model';
import { GoogleAuthService } from '../../app/services/google-auth/google-auth.service';
import { UserService } from '../services/user-service/user.service';
import { ChangeDetectorRef } from '@angular/core';
import { environment } from '../environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit, AfterViewInit {
  baseURLGoogle: string = environment.apiUrl + '/auth';
  baseURL: string = environment.apiUrl + '/users';
  showPopup = false;
  googleButtonRendered = false;
  popupMessage: string = 'Incorrect username or password. Please try again.';

  togglePopup(): void {
    this.showPopup = !this.showPopup;
  }

  loginForm = this.fb.group({
    email: ['', Validators.required],
    password: ['', Validators.required],
  });

  signupForm = this.fb.group({
    name: ['', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50)
    ]],
    surename: ['', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50)
    ]],
    email: ['', [
      Validators.required,
      Validators.email
    ]],
    password: ['', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(50),
      Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/)
    ]],
  });

  constructor(
    private userService: UserService,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder,
    private googleAuthService: GoogleAuthService,
    private cdr: ChangeDetectorRef
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
    const signupSuccess = this.googleAuthService.initializeLoginButton(signupContainer);

    this.googleButtonRendered = loginSuccess && signupSuccess;
    console.log('Google buttons initialized:', this.googleButtonRendered);

    this.cdr.detectChanges();
  }

  manualGoogleLogin(): void {
    const loginForm = document.querySelector('.login-form');
    if (loginForm && window.getComputedStyle(loginForm).display !== 'none') {
      this.googleAuthService.promptGoogleLogin();
    } else {
      this.googleAuthService.promptGoogleLogin();
    }
  }

  onLogin(event: Event): void {
    event.preventDefault();

    if (this.loginForm.invalid) return;

    const { email, password } = this.loginForm.value;

    this.userService.loginUser(email!, password!).subscribe({
      next: (response) => {

        console.log('Login successful');


        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Login error:', error);
        this.popupMessage = 'Incorrect username or password. Please try again.';
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
        if (error.status === 409) {
          this.popupMessage = 'This email is already in use.';
          this.togglePopup();
        } else {
          this.popupMessage = 'Incorrect username or password. Please try again.';
          this.togglePopup();
        }
      }
    });
  }

  private handleSuccessfulAuth(email: string): void {
    this.userService.setUser(email);
    this.router.navigate(['/home']);
  }
}
