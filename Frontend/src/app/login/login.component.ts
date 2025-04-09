import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { DataService } from '../data.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

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
    email: ['', Validators.required],
    password: ['', Validators.required],
  });

  constructor(
    private authService: DataService,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder
  ) { }

  loginUser(name: string, password: string) {
      return this.http.post('http://localhost:5131/User/login', { name, password }, { responseType: 'text' });
    }
  
    onLogin(event: Event): void {
      event.preventDefault();
      if (this.loginForm.valid) {
        const name = this.loginForm.value.email; 
        const password = this.loginForm.value.password;
        if (name && password) {
          this.loginUser(name, password).subscribe(response => {
            if (response === 'User found') {
              this.authService.setUser(name); 
              this.router.navigate(['/home']);
            } else {
              this.togglePopup(); 
            }
          });
        }
      }
    }

    signupUser(name: string, password: string) {
      return this.http.post('http://localhost:5131/User/register', { name, password }, { responseType: 'text' });
    }
    
    onSignup(event: Event): void {
      event.preventDefault();
    
      if (this.signupForm.valid) {
        const name = this.signupForm.value.email; 
        const password = this.signupForm.value.password;
        if (name && password) {
          this.signupUser(name, password).subscribe(response => {
            if (response === 'User created') {
              this.authService.setUser(name); 
              this.router.navigate(['/home']);
            } 
          });
        }
      }
    }
}
